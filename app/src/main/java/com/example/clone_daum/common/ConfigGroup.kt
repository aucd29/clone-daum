package com.example.clone_daum.common

import android.Manifest
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.common.camera.CameraInstance
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.Sitemap
import com.example.clone_daum.model.remote.WeatherDetail
import com.example.common.jsonParse
import com.example.common.runtimepermission.RuntimePermission
import com.example.common.stringId
import com.example.common.systemService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 15. <p/>
 */

@Singleton
class Config @Inject constructor(val context: Context) {
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

@Singleton
class PreloadConfig @Inject constructor(val daum: DaumService
                                        , val db: DbRepository
                                        , val dp: CompositeDisposable
                                        , val assets: AssetManager
                                        , val context: Context
) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    lateinit var tabLabelList: List<TabData>
    lateinit var brsSubMenuList: List<BrowserSubMenu>
    lateinit var naviSitemapList: List<Sitemap>

    // 초기 로딩 문제로 사용하는 곳에서 async 하게 call 하도록 수정
    // html parsing 이 sync 해서 느렸던건가?
    // DOM + XPATH 가 느리긴 하지만... -_ - [aucd29][2019. 1. 21.]
//    lateinit var weatherDetailList: List<WeatherDetail>
//    val realtimeIssueList: List<Pair<String, List<RealtimeIssue>>>
    init {
        dp.add(
            Observable.just(assets.open("res/brs_submenu.json").readBytes())
                .observeOn(Schedulers.io())
                .map { it.jsonParse<List<BrowserSubMenu>>() }
                .map {
                    it.forEach {
                        it.iconResid = context.stringId(it.icon)
                    }

                    it
                }
                .subscribe { brsSubMenuList = it })

        dp.add(
            Observable.just(assets.open("res/navi_sitemap.json").readBytes())
                .observeOn(Schedulers.io())
                .map { it.jsonParse<List<Sitemap>>() }
                .subscribe {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("PARSE OK : navi_sitemap.json")
                    }

                    naviSitemapList = it
                })

        dp.add(db.frequentlySiteDao.select().subscribeOn(Schedulers.io()).subscribe {
            if (it.size == 0) {
                // frequently_site.json 을 파싱 한 뒤에 그걸 디비에 넣는다.
                // 기본 값 생성하는 것.
                dp.add(
                    Observable.just(assets.open("res/frequently_site.json").readBytes())
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
    }

    fun daumMain() = daum.main().observeOn(Schedulers.io())

    fun weatherData(callback: (List<WeatherDetail>) -> Unit) {
        dp.add(Observable.just(assets.open("res/weather_default.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<WeatherDetail>>() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PARSE OK : weather_default.json")
                }

                callback.invoke(it)
            })
    }
}
