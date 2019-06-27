package com.example.clone_daum.di.module

import brigitte.di.dagger.module.OkhttpModule
import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [OkhttpModule::class])
class NetworkModule {
    companion object {
        val LOG_CLASS = NetworkModule::class.java
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger =
        LoggerFactory.getLogger(LOG_CLASS)

    @Provides
    @Singleton
    fun provideLogLevel() =
        HttpLoggingInterceptor.Level.BODY

}
