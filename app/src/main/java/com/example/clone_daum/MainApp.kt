package com.example.clone_daum

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.clone_daum.di.component.DaggerAppComponent
import com.example.clone_daum.model.Repository
import dagger.android.AndroidInjector
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
    }
    
    ////////////////////////////////////////////////////////////////////////////////////
    //
    // HasActivityInjector
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun activityInjector() = activityInjector
}