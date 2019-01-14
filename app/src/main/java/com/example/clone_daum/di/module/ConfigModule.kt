package com.example.clone_daum.di.module

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.GithubService
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.model.remote.Sitemap
import com.example.common.*
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.BiConsumer
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

@Module
class ConfigModule {
    @Singleton
    @Provides
    fun provideConfig(context: Context) = Config(context)

    @Singleton
    @Provides
    fun providePreloadConfig(github: GithubService, dmMain: DaumService,
                             db: DbRepository, dp: CompositeDisposable,
                             assetManager: AssetManager, context: Context) =
        PreloadConfig(github, dmMain, db, dp, assetManager, context)
}

////////////////////////////////////////////////////////////////////////////////////
//
// Config
//
////////////////////////////////////////////////////////////////////////////////////

class Config(val context: Context) {
    val USER_AGENT: String
    val ACTION_BAR_HEIGHT: Float
    val SCREEN = Point()
    val STATUS_BAR_HEIGHT: Int

    init {
        //
        // USER AGENT
        //
        val release = Build.VERSION.RELEASE
        val country = Locale.getDefault().country
        val language = Locale.getDefault().language
        val param = "service"   // LoginActorDeleteToken
        val version = BuildConfig.VERSION_NAME

        USER_AGENT = "DaumMobileApp (Linux; U; Android $release; $country-$language) $param/$version"

        //
        // ACTION BAR
        //

        val ta = context.theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize))
        ACTION_BAR_HEIGHT =  ta.getDimension(0, 0f)
        ta.recycle()

        //
        // W / H
        //
        context.systemService(WindowManager::class.java)?.defaultDisplay?.getSize(SCREEN)

        //
        // STATUS_BAR_HEIGHT
        //
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        STATUS_BAR_HEIGHT = if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// PreloadConfig
//
////////////////////////////////////////////////////////////////////////////////////

class PreloadConfig(github: GithubService, val daum: DaumService,
                    db: DbRepository, dp: CompositeDisposable, assets: AssetManager,
                    context: Context) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    lateinit var tabLabelList: List<TabData>
    lateinit var brsSubMenu: List<BrowserSubMenu>
    lateinit var naviSitemap: List<Sitemap>
    lateinit var realtimeIssueList: List<Pair<String, List<RealtimeIssue>>>

    init {
        dp.add(github.popularKeywordList().subscribeOn(Schedulers.io()).subscribe ({
            db.popularKeywordDao.run {
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETE ALL PopularKeyword")
                }
                deleteAll()

                val dataList = arrayListOf<PopularKeyword>()
                it.forEach {
                    dataList.add(PopularKeyword(keyword = it))
                }

                insertAll(dataList).subscribe {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("INSERTED PopularKeyword")
                    }
                }
            }
        }, { e -> mLog.error("ERROR: ${e.message}") }))

        dp.add(Observable.just(assets.open("res/brs_submenu.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<BrowserSubMenu>>() }
            .map {
                it.forEach {
                    it.iconResid = context.stringId(it.icon)
                }

                it
            }
            .subscribe { brsSubMenu = it })

        dp.add(Observable.just(assets.open("res/navi_sitemap.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<Sitemap>>() }
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PARSE OK : navi_sitemap.json")
                }

                naviSitemap = it
            })

        dp.add(db.frequentlySiteDao.select().subscribeOn(Schedulers.io()).subscribe {
            if (it.size == 0) {
                // frequently_site.json 을 파싱 한 뒤에 그걸 디비에 넣는다.
                // 기본 값 생성하는 것.
                dp.add(Observable.just(assets.open("res/frequently_site.json").readBytes())
                    .observeOn(Schedulers.io())
                    .map { it.jsonParse<List<FrequentlySite>>() }
                    .subscribe {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("PARSE OK : frequently_site.json ")
                        }

                        db.frequentlySiteDao.insertAll(it).subscribe {
                            if (mLog.isDebugEnabled) {
                                mLog.debug("INSERTED FrequentlySite")
                            }
                        }
                    })
            }
        })

        tabLabelList = Observable.just(assets.open("res/tab.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<TabData>>() }
            .blockingFirst()

        loadRealtimeIssue()
    }

    fun loadRealtimeIssue() {
        val mainHtml = daum.main().blockingFirst()

        // 전체 이슈 하나만 있는 줄 알았더니만... 3개 더 있네.. ㄷ ㄷ ㄷ
        realtimeIssueList = parseRealtimeIssue(mainHtml)
    }

    private fun parseRealtimeIssue(main: String): List<Pair<String, List<RealtimeIssue>>> {
        if (mLog.isDebugEnabled) {
            mLog.debug("PARSE REALTIME ISSUE")
        }

        val f = main.indexOf("""<div id="footerHotissueRankingDiv_channel_news1">""")
        val e = main.indexOf("""<div class="d_foot">""")

        // https://www.w3schools.com/tags/ref_urlencode.asp
        var issue = main.substring(f, e)
            .replace(" class='keyissue_area '", "")
            .replace(" class='keyissue_area on'", "")
            .replace("(\n|\t)".toRegex(), "")
            .replace("&amp;", "%26")
            .replace("&", "%26")
        issue = issue.substring(0, issue.length - "</div>".length)

        val parse = RealtimeIssueParser()
        parse.loadXml(issue)

        if (mLog.isDebugEnabled) {
            parse.realtimeIssueList.forEach({
                mLog.debug("${it.first} : (${it.second.size})")
            })
        }

        return parse.realtimeIssueList
    }
}

class RealtimeIssueParser: BaseXPath() {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueParser::class.java)
        private const val REMOVE_WORD    = " 이슈검색어"
        private const val REALTIME_ISSUE = "실시간이슈"
    }

    val realtimeIssueList = arrayListOf<Pair<String, List<RealtimeIssue>>>()

// 파싱을 방지하려고 url 을 이래 놓은건가?
// --
// SAMPLE DATA
// --
//    <div id="footerHotissueRankingDiv_channel_news1">
//    <div class='keyissue_area '>
//    <strong class="screen_out">전체 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%A7%84%ED%98%95&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">진형</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%99%8D%EC%88%98%ED%98%84&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">홍수현</span></a></li>
//    </ul>
//    </div>
//
//    <div class='keyissue_area on'>
//    <strong class="screen_out">뉴스 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%B2%AD%EB%85%84+%EB%82%B4%EC%9D%BC%EC%B1%84%EC%9B%80%EA%B3%B5%EC%A0%9C&amp;DA=13I&amp;pin=news" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">청년 내일채움공제</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EA%B0%95%EC%95%84%EC%A7%80+3%EB%A7%88%EB%A6%AC&amp;DA=13I&amp;pin=news" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">강아지 3마리</span></a></li>
//    </ul>
//    </div>
//
//    <div class='keyissue_area '>
//    <strong class="screen_out">연예 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%95%98%EC%97%B0%EC%88%98+%EB%85%BC%EB%9E%80&amp;DA=13K&amp;pin=entertain" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">하연수 논란</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EB%B0%95%EC%9D%B8%ED%99%98&amp;DA=13K&amp;pin=entertain" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">박인환</span></a></li>
//    </ul>
//    </div>
//
//    <div class='keyissue_area '>
//    <strong class="screen_out">스포츠 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%9D%B4%EB%9D%BC%ED%81%AC+%EB%B2%A0%ED%8A%B8%EB%82%A8&amp;DA=13J&amp;pin=sports" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">이라크 베트남</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%99%A9%ED%9D%AC%EC%B0%AC&amp;DA=13J&amp;pin=sports" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">황희찬</span></a></li>
//    </ul>
//    </div>
//    </div>
//    </div>

    override fun parsing() {
        var exp = "count(//strong)"
        val strongCnt = int(exp)

        var k = 1
        while(k <= strongCnt) {
            exp = "/div/div[$k]/strong/text()"
            var label = string(exp).replace(REMOVE_WORD, "")
            if ("전체" == label) {
                label = REALTIME_ISSUE
            }

            if (mLog.isDebugEnabled) {
                mLog.debug("LABEL : $label")
            }

            exp = "count(/div/div[$k]/ul)"
            val ulCnt = int(exp)

            val issueList = arrayListOf<RealtimeIssue>()

            var i = 1
            var count = 1
            while (i <= ulCnt) {
                exp = "count(/div/div[$k]/ul[$i]/li)"
                val liCnt = int(exp)

                var j = 1
                while (j <= liCnt) {
                    exp = "/div/div[$k]/ul[$i]/li[$j]/a/@href"
                    val url = string(exp).urldecode()

                    exp = "/div/div[$k]/ul[$i]/li[$j]/a/span[@class='txt_issue']/text()"
                    val text = string(exp)

                    issueList.add(RealtimeIssue(count, url, text))

                    ++count
                    ++j
                }

                ++i
            }

            realtimeIssueList.add(label to issueList)
            ++k
        }
    }
}
