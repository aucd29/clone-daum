package com.example.clone_daum.di.module

import com.example.clone_daum.di.module.common.OkhttpModule
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [OkhttpModule::class])
class NetworkModule {
    companion object {
        val LOG_CLASS = NetworkModule::class.java
    }

    @Provides
    @Singleton
    fun provideLogger() =
        LoggerFactory.getLogger(LOG_CLASS)

    @Provides
    @Singleton
    fun provideLogLevel() =
        HttpLoggingInterceptor.Level.BODY

}
