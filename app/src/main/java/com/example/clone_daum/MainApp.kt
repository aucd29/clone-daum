package com.example.clone_daum

import android.app.Activity
import android.os.Build
import androidx.multidex.MultiDexApplication
import com.example.clone_daum.di.component.DaggerAppComponent
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.remote.GithubService
import com.squareup.leakcanary.LeakCanary
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
    companion object {
        private val mLog = LoggerFactory.getLogger(MainApp::class.java)
    }

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var preConfig: PreloadConfig

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        LeakCanary.install(this);

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // HasActivityInjector
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun activityInjector() = activityInjector
}