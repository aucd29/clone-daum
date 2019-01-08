package com.example.common.di.module

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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
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
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
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

    // https://stackoverflow.com/questions/36523972/how-to-get-string-response-from-retrofit2
    @Provides
    @Singleton
    fun provideScalarsConverterFactory() =
        ScalarsConverterFactory.create()

    @Provides
    fun provideRetrofit(rxAdapter: RxJava2CallAdapterFactory,
                        jacksonFactory: JacksonConverterFactory,
                        scalarsConverterFactory: ScalarsConverterFactory,
                        okhttpclient: OkHttpClient) =
        Retrofit.Builder()
            .addCallAdapterFactory(rxAdapter)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(jacksonFactory)
            .client(okhttpclient)
}