package com.example.clone_daum.di.module

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.remote.GithubService
import com.example.common.systemService
import dagger.Module
import dagger.Provides
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
    fun providePreloadConfig(dm: GithubService, db: DbRepository, dp: CompositeDisposable) =
        PreloadConfig(dm, db, dp)
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

class PreloadConfig(dm: GithubService, db: DbRepository, dp: CompositeDisposable) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PreloadConfig::class.java)
    }

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
    }
}