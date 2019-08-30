package com.example.clone_daum.di.module

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import brigitte.di.dagger.module.FragmentActivityModule
import brigitte.di.dagger.scope.ActivityScope
import com.example.clone_daum.MainActivity
import com.example.clone_daum.MainApp
import com.example.clone_daum.ui.ViewController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module(includes = [])
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [
        FragmentModule::class,
        MainActivityModule::class
    ])
    abstract fun contributeMainActivity(): MainActivity
}

// https://stackoverflow.com/questions/48533899/how-to-inject-members-in-baseactivity-using-dagger-android
@Module(includes = [
    FragmentActivityModule::class
])
class MainActivityModule {
    @Provides
    fun provideFragmentActivity(activity: MainActivity): FragmentActivity =
        activity

    @Provides
    fun provideFragmentManager(activity: MainActivity) =
        activity.supportFragmentManager
}

//abstract class MainActivityModule {
//    @Binds
//    abstract fun bindFragmentActivity(activity: MainActivity): FragmentActivity
//}