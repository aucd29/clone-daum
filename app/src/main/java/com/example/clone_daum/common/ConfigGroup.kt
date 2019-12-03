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
import java.util.function.BiFunction

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
    private val mDaum: DaumService,
    private val mDb: DbRepository,
    private val mDisposable: CompositeDisposable,
    private val mAssetManager: AssetManager,
    private val mContext: Context
) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    lateinit var tabLabelList: List<TabData>
    lateinit var brsSubMenuList: List<BrowserSubMenu>
    lateinit var naviSitemapList: List<Sitemap>

    init {
        val submenu = Single.just(mAssetManager.open("res/brs_submenu.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { it.jsonParse<List<BrowserSubMenu>>() }
            .map {
                it.forEach { f ->
                    f.iconResid = mContext.stringId(f.icon)
                }

                it
            }

        val sitemap = Single.just(mAssetManager.open("res/navi_sitemap.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { it.jsonParse<List<Sitemap>>() }

        val tabList = Single.just(mAssetManager.open("res/tab.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { it.jsonParse<List<TabData>>() }

        val frequentlySite = mDb.frequentlySiteDao.select().subscribeOn(Schedulers.io())

        val defaultFrequentlySite = Single.just(mAssetManager.open("res/frequently_site.json").readBytes())
            .subscribeOn(Schedulers.io())
            .map { s -> s.jsonParse<List<FrequentlySite>>() }

        mDisposable.add(submenu.subscribe { it ->
                brsSubMenuList = it

                if (mLog.isDebugEnabled) {
                    mLog.debug("LOADED BrowserSubMenu")
                }
            })

        mDisposable.add(sitemap.subscribe { it ->
                naviSitemapList = it

                if (mLog.isDebugEnabled) {
                    mLog.debug("LOADED Sitemap")
                }
            })

        mDisposable.add(frequentlySite.subscribe {
                if (it.isEmpty()) {
                    // frequently_site.json 을 파싱 한 뒤에 그걸 디비에 넣는다.
                    // 기본 값 생성하는 것.
                    mDisposable.add(defaultFrequentlySite.subscribe { list ->
                            if (mLog.isDebugEnabled) {
                                mLog.debug("LOADED DEFAULT FrequentlySite")
                            }

                            mDb.frequentlySiteDao.insertAll(list).subscribe {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("INSERTED FrequentlySite")
                                }
                            }
                        })
                }
            })

        tabLabelList = tabList.blockingGet()
        if (mLog.isDebugEnabled) {
            mLog.debug("LOADED TabData")
        }
    }

    fun daumMain(): Observable<String> = mDaum.main()

    fun weatherData(callback: (List<WeatherDetail>) -> Unit) {
        mDisposable.add(Observable.just(mAssetManager.open("res/weather_default.json").readBytes())
            .subscribeOn(Schedulers.io())
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
