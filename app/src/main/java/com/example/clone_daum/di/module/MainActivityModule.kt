package com.example.clone_daum.di.module

import com.example.clone_daum.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = arrayOf(DaumModule::class))
    abstract fun contributeMainActivity(): MainActivity
}