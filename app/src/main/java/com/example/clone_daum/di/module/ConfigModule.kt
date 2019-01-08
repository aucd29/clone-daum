package com.example.clone_daum.di.module

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.GithubService
import com.example.clone_daum.model.remote.Sitemap
import com.example.common.*
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.*
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

class PreloadConfig(github: GithubService, dmMain: DaumService,
                    db: DbRepository, dp: CompositeDisposable, assets: AssetManager,
                    context: Context) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    val tabLabelList: List<TabData>
    lateinit var brsSubMenu: List<BrowserSubMenu>
    lateinit var naviSitemap: List<Sitemap>
    lateinit var realtimeIssue: List<RealtimeIssue>

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

        dp.add(dmMain.main().subscribe {
            parseRealtimeIssue(it)
        })

        tabLabelList = Observable.just(assets.open("res/tab.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<TabData>>() }
            .blockingFirst()
    }

    private fun parseRealtimeIssue(main: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("PARSE REALTIME ISSUE")
        }

        val f = main.indexOf("""<div class='keyissue_area '>""")
        val e = main.indexOf("""</div>""", f)

        // 이건 파싱을 방지하려고 이래 놓은건가? (혹은 엿먹으라고.. -_ -?)
        // https://www.w3schools.com/tags/ref_urlencode.asp
        val issue = main.substring(f, e + "</div>".length).replace(" class='keyissue_area '", "")
            .replace("(\n|\t)".toRegex(), "").replace("&amp;", "%26")
            .replace("&", "%26")

        if (mLog.isDebugEnabled) {
            mLog.debug("ISSUE : $issue")
        }

        val parse = RealtimeIssueParse()
        parse.loadXml(issue)

        if (mLog.isDebugEnabled) {
            mLog.debug("COUNT : ${parse.realtimeIssueList.size}")
        }

        realtimeIssue = parse.realtimeIssueList
    }
}

//        <div class='keyissue_area '>
//        <strong class="screen_out">전체 이슈검색어</strong>
//        <ul class="list_issue">
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%A7%84%ED%98%95&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">진형</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%95%98%EC%97%B0%EC%88%98&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">2</em><span class="screen_out">위</span><span class="txt_issue">하연수</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%EB%A7%B9%EC%9C%A0%EB%82%98&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">3</em><span class="screen_out">위</span><span class="txt_issue">맹유나</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%EB%85%B8%EC%98%81%EB%AF%BC&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">4</em><span class="screen_out">위</span><span class="txt_issue">노영민</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%8C%8C%EC%9D%B8%ED%85%8D&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">5</em><span class="screen_out">위</span><span class="txt_issue">파인텍</span></a></li>
//        </ul>
//        <ul class="list_issue">
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%99%8D%EC%88%98%ED%98%84&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">홍수현</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%9D%B4%ED%95%98%EC%9D%B4&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">7</em><span class="screen_out">위</span><span class="txt_issue">이하이</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%A7%84%EC%98%81&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">8</em><span class="screen_out">위</span><span class="txt_issue">진영</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%EB%A7%88%EC%9D%B4%ED%81%AC%EB%A1%9C%EB%8B%B7&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">9</em><span class="screen_out">위</span><span class="txt_issue">마이크로닷</span></a></li>
//        <li><a href="https://m.search.daum.net/search?w=tot&q=%EB%82%B4%EC%95%88%EC%9D%98+%EA%B7%B8%EB%86%88&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">10</em><span class="screen_out">위</span><span class="txt_issue">내안의 그놈</span></a></li>
//        </ul>
//        </div>
class RealtimeIssueParse: BaseXPath() {
    val realtimeIssueList = arrayListOf<RealtimeIssue>()

    override fun parsing() {
        var exp = "count(//ul)"
        val ulCnt = int(exp)

        exp = "count(//li)"
        val liCnt = int(exp) / ulCnt

        var i = 1
        while (i <= ulCnt) {
            var j = 1
            while (j <= liCnt) {
                exp = "//ul[$i]/li[$j]/a/@href"
                val url = string(exp).urldecode()

                exp = "//ul[$i]/li[$j]/a/span[@class='txt_issue']/text()"
                val text = string(exp)

                realtimeIssueList.add(RealtimeIssue(url, text))

                ++j
            }

            ++i
        }
    }
}

data class RealtimeIssue (
    val url: String,
    val text: String
)