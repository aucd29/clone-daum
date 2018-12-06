package com.example.clone_daum.di.module

import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.ui.main.MainFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import retrofit2.Retrofit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */


@Module(includes = arrayOf(
    NetworkModule::class
))
abstract class DaumModule {
    @Provides
    fun provideDaum(retrofit: Retrofit) =
        retrofit.create(DaumService::class.java)

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment
}