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
import com.example.clone_daum.model.remote.GithubService
import com.example.clone_daum.model.remote.Sitemap
import com.example.common.jsonParse
import com.example.common.stringId
import com.example.common.systemService
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
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
    fun providePreloadConfig(dm: GithubService, db: DbRepository, dp: CompositeDisposable,
                             assetManager: AssetManager, context: Context) =
        PreloadConfig(dm, db, dp, assetManager, context)
}

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

class PreloadConfig(dm: GithubService, db: DbRepository, dp: CompositeDisposable, assets: AssetManager,
                    context: Context) {

    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

    val tabLabelList: List<TabData>
    lateinit var brsSubMenu: List<BrowserSubMenu>
    lateinit var naviSitemap: List<Sitemap>

    init {
        dp.add(dm.popularKeywordList().subscribeOn(Schedulers.io()).subscribe ({
            db.popularKeywordDao.run {
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETE ALL POPULAR KEYWORD")
                }
                deleteAll()

                it.forEach {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("INSERT POPULAR KEYWORD $it")
                    }
                    insert(PopularKeyword(keyword = it)).subscribe()
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

        dp.add(Observable.just(assets.open("res/navi_services.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<Sitemap>>() }
            .subscribe { naviSitemap = it })

        dp.add(db.frequentlySiteDao.select().subscribe {
            if (it.size == 0) {
                val ob1 = db.frequentlySiteDao.insert(FrequentlySite(title = "KAKAO TV"
                    , url = "http://m.tv.kakao.com", count = 0))
                val ob2 = db.frequentlySiteDao.insert(FrequentlySite(title = "DIC"
                    , url = "http://m.dic.kakao.com", count = 0))
                val ob3 = db.frequentlySiteDao.insert(FrequentlySite(title = "MAP"
                    , url = "http://m.map.kakao.com", count = 0))
                val ob4 = db.frequentlySiteDao.insert(FrequentlySite(title = "TSTORY"
                    , url = "http://m.tstory.com", count = 0))

                Flowable.just(ob1, ob2, ob3, ob4).subscribe {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("INSERT PRELOAD DATA (CONNECTED SITE)")
                    }
                }
            }
        })

        tabLabelList = Observable.just(assets.open("res/tab.json").readBytes())
            .observeOn(Schedulers.io())
            .map { it.jsonParse<List<TabData>>() }
            .blockingFirst()
    }
}