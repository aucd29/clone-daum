package com.example.clone_daum.common

import android.Manifest
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Point
import android.os.Build
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.R
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.Sitemap
import com.example.clone_daum.model.remote.WeatherDetail
import brigitte.runtimepermission.RuntimePermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import android.view.*
import brigitte.*
import io.reactivex.Single

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 15. <p/>
 */

@Singleton
class Config @Inject constructor(val context: Context) {
    val USER_AGENT: String
    val ACTION_BAR_HEIGHT: Float
    val SCREEN = Point()
    var HAS_PERMISSION_GPS = false
    var DEFAULT_LOCATION   = "서울"
    val SEARCH_ICON: Int

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

        ACTION_BAR_HEIGHT = context.actionBarSize()

        //
        // W / H
        //
        val windowManager = context.systemService<WindowManager>()
        windowManager?.defaultDisplay?.getSize(SCREEN)

        //
        // PERMISSION
        //
        HAS_PERMISSION_GPS = RuntimePermission.checkPermissions(context
            , permissions = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION))

        val rightNow = Calendar.getInstance()
        SEARCH_ICON = when (rightNow.get(Calendar.HOUR_OF_DAY) % 4) {
            0    -> R.drawable.ic_mic_none_black_24dp
            1    -> R.drawable.ic_music_note_black_24dp
            2    -> R.drawable.ic_spa_black_24dp
            else -> R.drawable.ic_visibility_black_24dp
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// PreloadConfig
//
////////////////////////////////////////////////////////////////////////////////////

@Singleton
class PreloadConfig @Inject constructor(
    private val daum: DaumService,
    private val db: DbRepository,
    private val disposable: CompositeDisposable,
    private val assetManager: AssetManager,
    private val context: Context
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    lateinit var tabLabelList: List<TabData>
    lateinit var brsSubMenuList: List<BrowserSubMenu>
    lateinit var naviSitemapList: List<Sitemap>

    init {
        val submenu = Single.just(assetManager.open("res/brs_submenu.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { it.jsonParse<List<BrowserSubMenu>>() }
            .map {
                it.forEach { f ->
                    f.iconResid = context.stringId(f.icon)
                }

                it
            }

        val sitemap = Single.just(assetManager.open("res/navi_sitemap.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { it.jsonParse<List<Sitemap>>() }

        val tabList = Single.just(assetManager.open("res/tab.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { it.jsonParse<List<TabData>>() }

        val frequentlySite = db.frequentlySiteDao.select().subscribeOn(Schedulers.io())

        val defaultFrequentlySite = Single.just(assetManager.open("res/frequently_site.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { s -> s.jsonParse<List<FrequentlySite>>() }

        disposable.add(submenu.subscribe { it ->
                brsSubMenuList = it

                if (logger.isDebugEnabled) {
                    logger.debug("LOADED BrowserSubMenu")
                }
            })

        disposable.add(sitemap.subscribe { it ->
                naviSitemapList = it

                if (logger.isDebugEnabled) {
                    logger.debug("LOADED Sitemap")
                }
            })

        disposable.add(frequentlySite.subscribe {
                if (it.isEmpty()) {
                    // frequently_site.json 을 파싱 한 뒤에 그걸 디비에 넣는다.
                    // 기본 값 생성하는 것.
                    disposable.add(defaultFrequentlySite.subscribe { list ->
                            if (logger.isDebugEnabled) {
                                logger.debug("LOADED DEFAULT FrequentlySite")
                            }

                            db.frequentlySiteDao.insertAll(list).subscribe {
                                if (logger.isDebugEnabled) {
                                    logger.debug("INSERTED FrequentlySite")
                                }
                            }
                        })
                }
            })

        tabLabelList = tabList.blockingGet()
        if (logger.isDebugEnabled) {
            logger.debug("LOADED TabData")
        }
    }

    fun daumMain(): Observable<String> = daum.main()

    fun weatherData(callback: (List<WeatherDetail>) -> Unit) {
        disposable.add(Observable.just(assetManager.open("res/weather_default.json").readBytes())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<WeatherDetail>>() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (logger.isDebugEnabled) {
                    logger.debug("PARSE OK : weather_default.json")
                }

                callback.invoke(it)
            })
    }
}
