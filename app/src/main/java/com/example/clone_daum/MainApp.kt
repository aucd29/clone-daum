package com.example.clone_daum

import android.app.Activity
import android.os.Build
import androidx.multidex.MultiDexApplication
import com.example.clone_daum.di.component.DaggerAppComponent
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.remote.GithubService
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 26. <p/>
 */

class MainApp : MultiDexApplication(), HasActivityInjector {
    private val mLog = LoggerFactory.getLogger(MainApp::class.java)

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var dm: GithubService
    @Inject lateinit var db: DbRepository

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)

        loadNetworkData()
        makeUserAgent()
    }

    private fun loadNetworkData() {
        dm.popularKeywordList().subscribeOn(Schedulers.io()).subscribe ({
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
        }, { e -> mLog.error("ERROR: ${e.message}") })
    }

    private fun makeUserAgent() {
        // at http protocol utils
        // build.version.release, Locale.getDefault().getLanguage()
        // Locale.getDefault().getCountry()
        // paramString, AppVersion.getVersion(paramContext)

        val release = Build.VERSION.RELEASE
        val country = Locale.getDefault().country
        val language = Locale.getDefault().language
        val param = "service"   // LoginActorDeleteToken
        val version = BuildConfig.VERSION_NAME

        Config.USER_AGENT = "DaumMobileApp (Linux; U; Android $release; $country-$language) $param/$version"
    }
    
    ////////////////////////////////////////////////////////////////////////////////////
    //
    // HasActivityInjector
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun activityInjector() = activityInjector
}