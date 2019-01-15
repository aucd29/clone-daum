package com.example.clone_daum.di.module

import android.Manifest
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Point
import android.location.Geocoder
import android.os.Build
import android.view.WindowManager
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.model.remote.*
import com.example.common.*
import com.example.common.runtimepermission.RuntimePermission
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

    var HAS_PERMISSION_GPS = false
    var DEFAULT_LOCATION   = "서울"

    init {
        //
        // USER AGENT
        //
        val release = Build.VERSION.RELEASE
        val country = Locale.getDefault().country
        val language= Locale.getDefault().language
        val param          = "service"   // LoginActorDeleteToken
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

        //
        // PERMISSION
        //
        HAS_PERMISSION_GPS = RuntimePermission.checkPermissions(context
            , permissions = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// PreloadConfig
//
////////////////////////////////////////////////////////////////////////////////////

class PreloadConfig(github: GithubService, val daum: DaumService,
                    db: DbRepository, dp: CompositeDisposable, assets: AssetManager,
                    val context: Context) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    lateinit var tabLabelList: List<TabData>
    lateinit var brsSubMenuList: List<BrowserSubMenu>
    lateinit var naviSitemapList: List<Sitemap>
    lateinit var realtimeIssueList: List<Pair<String, List<RealtimeIssue>>>
    lateinit var weatherDetailList: List<WeatherDetail>

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
            .subscribe { brsSubMenuList = it })

        dp.add(Observable.just(assets.open("res/navi_sitemap.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<Sitemap>>() }
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PARSE OK : navi_sitemap.json")
                }

                naviSitemapList = it
            })

        dp.add(Observable.just(assets.open("res/weather_default.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<WeatherDetail>>() }
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PARSE OK : weather_default.json")
                }

                weatherDetailList = it
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

        htmlParse()
    }

    fun htmlParse() {
        val mainHtml = daum.main().blockingFirst()

        // 전체 이슈 하나만 있는 줄 알았더니만... 3개 더 있네.. ㄷ ㄷ ㄷ
        realtimeIssueList = parseRealtimeIssue(mainHtml)
    }

    private fun parseRealtimeIssue(main: String): List<Pair<String, List<RealtimeIssue>>> {
        if (mLog.isDebugEnabled) {
            mLog.debug("PARSE REALTIME ISSUE")
        }

        val parse = RealtimeIssueParser()
        val f = main.indexOf("""<div id="footerHotissueRankingDiv_channel_news1">""")
        val e = main.indexOf("""<div class="d_foot">""")

        if (f == -1 || e == -1) {
            mLog.error("ERROR: INVALID HTML DATA f = $f, e = $e")

            return parse.realtimeIssueList
        }

        // https://www.w3schools.com/tags/ref_urlencode.asp
        var issue = main.substring(f, e)
            .replace(" class='keyissue_area '", "")
            .replace(" class='keyissue_area on'", "")
            .replace("(\n|\t)".toRegex(), "")
            .replace("&amp;", "%26")
            .replace("&", "%26")
        issue = issue.substring(0, issue.length - "</div>".length)

        parse.loadXml(issue)

        if (mLog.isDebugEnabled) {
            parse.realtimeIssueList.forEach({
                mLog.debug("${it.first} : (${it.second.size})")
            })
        }

        return parse.realtimeIssueList
    }

    private fun loadGeoCode() {
        val geocoder = Geocoder(context, Locale.getDefault())

    }
}

