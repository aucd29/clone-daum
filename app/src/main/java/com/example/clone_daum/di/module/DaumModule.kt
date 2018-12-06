package com.example.clone_daum.di.module

import com.example.clone_daum.di.module.common.NetworkModule
import com.example.clone_daum.model.remote.DaumService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */


@Module(includes = arrayOf(NetworkModule::class))
class DaumModule {
    @Provides
    fun provideDaum(retrofit: Retrofit) =
        retrofit.create(DaumService::class.java)
}