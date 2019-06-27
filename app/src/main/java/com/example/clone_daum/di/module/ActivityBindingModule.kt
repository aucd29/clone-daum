package com.example.clone_daum.di.module

import androidx.fragment.app.FragmentManager
import com.example.clone_daum.MainActivity
import com.example.clone_daum.ui.ViewController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module(includes = [])
abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class
        , MainActivityModule::class
    ])
    abstract fun contributeMainActivity(): MainActivity

    @Singleton
    @Binds
    abstract fun bindViewController(controller: ViewController): Any
}

// https://stackoverflow.com/questions/48533899/how-to-inject-members-in-baseactivity-using-dagger-android
@Module
class MainActivityModule {
    @Provides
    fun provideFragmentManager(activity: MainActivity): FragmentManager =
        activity.supportFragmentManager
}
