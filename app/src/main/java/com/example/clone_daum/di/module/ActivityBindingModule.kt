package com.example.clone_daum.di.module

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import brigitte.di.dagger.module.FragmentActivityModule
import com.example.clone_daum.MainActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module(includes = [])
abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = [
        FragmentModule::class,
        MainActivityModule::class
    ])

    abstract fun contributeMainActivity(): MainActivity
}

// https://stackoverflow.com/questions/48533899/how-to-inject-members-in-baseactivity-using-dagger-android
@Module(includes = [FragmentActivityModule::class])
class MainActivityModule {
    @Provides
    fun provideFragmentManager(activity: MainActivity): FragmentManager =
        activity.supportFragmentManager

    @Provides
    fun provideFragmentActivity(activity: MainActivity): FragmentActivity =
        activity
}
