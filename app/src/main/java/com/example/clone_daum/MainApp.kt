package com.example.clone_daum

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.example.clone_daum.di.component.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.github.inflationx.viewpump.ViewPump
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 26. <p/>
 */

class MainApp : MultiDexApplication(), HasActivityInjector {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainApp::class.java)

        fun watcher(context: Context)
                = (context.applicationContext as MainApp).refWatcher
    }

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var viewPump: ViewPump

    lateinit var refWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        // https://stackoverflow.com/questions/45677769/how-to-fix-inputmethodmanager-leaks-in-android
        refWatcher = LeakCanary.install(this)

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)

        ViewPump.init(viewPump)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // HasActivityInjector
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun activityInjector() = activityInjector
}