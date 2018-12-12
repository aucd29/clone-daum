package com.example.clone_daum.di.module.common

import com.example.common.Json
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 10. <p/>
 */

@Module
class OkhttpModule {
    @Provides
    @Singleton
    fun provideOkhttpClient(logInterceptor: HttpLoggingInterceptor,
                            logLevel: HttpLoggingInterceptor.Level) : OkHttpClient {
        logInterceptor.level = logLevel

        return OkHttpClient.Builder()
            .addInterceptor(logInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(logger: Logger) =
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            if (logger.isDebugEnabled) {
                logger.debug(it)
            }
        })


    @Provides
    @Singleton
    fun provideRxJava2CallAdapterFactory() =
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    @Provides
    @Singleton
    fun provideJacksonConverterFactory() =
        JacksonConverterFactory.create(Json.mapper)

    @Provides
    fun provideRetrofit(rxAdapter: RxJava2CallAdapterFactory,
                        jacksonFactory: JacksonConverterFactory,
                        okhttpclient: OkHttpClient) =
        Retrofit.Builder()
            .addCallAdapterFactory(rxAdapter)
            .addConverterFactory(jacksonFactory)
            .client(okhttpclient)
}