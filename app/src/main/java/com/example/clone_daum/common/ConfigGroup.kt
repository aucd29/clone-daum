package com.example.clone_daum.common

import android.Manifest
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.R
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
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 15. <p/>
 */

@Singleton
class Config @Inject constructor(val context: Context) {
    val USER_AGENT: String
    val ACTION_BAR_HEIGHT: Float
    val SCREEN = Point()
    val STATUS_BAR_HEIGHT: Int
    val SOFT_BUTTON_BAR_HEIGHT: Int

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

        val ta = context.theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize))
        ACTION_BAR_HEIGHT =  ta.getDimension(0, 0f)
        ta.recycle()

        //
        // W / H
        //
        val windowManager = context.systemService<WindowManager>()
        windowManager?.defaultDisplay?.getSize(SCREEN)

        //
        // STATUS_BAR_HEIGHT
        //
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        STATUS_BAR_HEIGHT = if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0

        //
        // https://stackoverflow.com/questions/29398929/how-get-height-of-the-status-bar-and-soft-key-buttons-bar
        //
        SOFT_BUTTON_BAR_HEIGHT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(metrics)
            val usableHeight = metrics.heightPixels

            windowManager?.defaultDisplay?.getRealMetrics(metrics)
            val realHeight = metrics.heightPixels

            if (realHeight > usableHeight)
                 realHeight - usableHeight;
            else
                0
        } else 0

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

    fun splashMargin(): Int {
        // softkey 가 존재 시 안맞는 부분 존재
        val statusMargin = STATUS_BAR_HEIGHT * -1 / 2
        val bottomButtonMargin = if (SOFT_BUTTON_BAR_HEIGHT == 0) 0 else SOFT_BUTTON_BAR_HEIGHT / 2

        return statusMargin + bottomButtonMargin
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// PreloadConfig
//
////////////////////////////////////////////////////////////////////////////////////

@Singleton
class PreloadConfig @Inject constructor(private val mDaum: DaumService
    , private val mDb: DbRepository
    , private val mDisposable: CompositeDisposable
    , private val mAssetManager: AssetManager
    , private val mContext: Context
) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    lateinit var tabLabelList: List<TabData>
    lateinit var brsSubMenuList: List<BrowserSubMenu>
    lateinit var naviSitemapList: List<Sitemap>

    init {
        mDisposable.add(Observable.just(mAssetManager.open("res/brs_submenu.json").readBytes())
                .observeOn(Schedulers.io())
                .map { it.jsonParse<List<BrowserSubMenu>>() }
                .map {
                    it.forEach {
                        it.iconResid = mContext.stringId(it.icon)
                    }

                    it
                }
                .subscribe { brsSubMenuList = it })

        mDisposable.add(Observable.just(mAssetManager.open("res/navi_sitemap.json").readBytes())
                .observeOn(Schedulers.io())
                .map { it.jsonParse<List<Sitemap>>() }
                .subscribe {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("PARSE OK : navi_sitemap.json")
                    }

                    naviSitemapList = it
                })

        mDisposable.add(mDb.frequentlySiteDao.select().subscribeOn(Schedulers.io()).subscribe {
            if (it.size == 0) {
                // frequently_site.json 을 파싱 한 뒤에 그걸 디비에 넣는다.
                // 기본 값 생성하는 것.
                mDisposable.add(Observable.just(mAssetManager.open("res/frequently_site.json").readBytes())
                        .observeOn(Schedulers.io())
                        .map { it.jsonParse<List<FrequentlySite>>() }
                        .subscribe {
                            if (mLog.isDebugEnabled) {
                                mLog.debug("PARSE OK : frequently_site.json ")
                            }

                            mDb.frequentlySiteDao.insertAll(it).subscribe {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("INSERTED FrequentlySite")
                                }
                            }
                        })
            }
        })

        tabLabelList = Observable.just(mAssetManager.open("res/tab.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<TabData>>() }
            .blockingFirst()
    }

    fun daumMain() = mDaum.main().observeOn(Schedulers.io())

    fun weatherData(callback: (List<WeatherDetail>) -> Unit) {
        mDisposable.add(Observable.just(mAssetManager.open("res/weather_default.json").readBytes())
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
