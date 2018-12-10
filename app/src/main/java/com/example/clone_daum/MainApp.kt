package com.example.clone_daum

import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.example.clone_daum.di.component.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 26. <p/>
 */

class MainApp : MultiDexApplication(), HasActivityInjector {
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

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