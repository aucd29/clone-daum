package com.example.clone_daum.di.module

import androidx.fragment.app.FragmentManager
import com.example.clone_daum.MainActivity
import com.example.clone_daum.ui.ViewController
import com.example.common.di.module.ViewModelFactoryModule
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module(includes = [])
abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class, MainActivityModule::class])
    abstract fun contributeMainActivity(): MainActivity
}

// https://stackoverflow.com/questions/48533899/how-to-inject-members-in-baseactivity-using-dagger-android
@Module
class MainActivityModule {
    @Provides
    fun provideFragmentManager(activity: MainActivity) =
        activity.supportFragmentManager

    @Provides
    fun provideViewController(manager: FragmentManager) = ViewController.get.apply {
        this.manager = manager
    }
}
