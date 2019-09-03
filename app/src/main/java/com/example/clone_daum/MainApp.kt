package com.example.clone_daum

import android.content.Context
import android.view.View
import androidx.multidex.MultiDex
import com.example.clone_daum.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.github.inflationx.viewpump.ViewPump
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 26. <p/>
 */

class MainApp : DaggerApplication() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainApp::class.java)
    }

    @Inject lateinit var viewPump: ViewPump

    override fun onCreate() {
        super.onCreate()

        mLog.error("START APP ${BuildConfig.APPLICATION_ID} (${BuildConfig.VERSION_NAME})")

        ViewPump.init(viewPump)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // DAGGER
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private val mComponent: AndroidInjector<MainApp> by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAppComponent.factory().create(this)
    }

    override fun applicationInjector() =
        mComponent
}