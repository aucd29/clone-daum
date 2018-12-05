package com.example.clone_daum.di.module
//
//import dagger.Module
//import dagger.Provides
//import io.reactivex.schedulers.Schedulers
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import retrofit2.Retrofit
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
//import retrofit2.converter.jackson.JacksonConverterFactory
//import javax.inject.Singleton
//
///**
// * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
// */
//
//@Module
//class NetworkModule(private val mBaseUrl: String, private val mLogLevel: HttpLoggingInterceptor.Level) {
//    @Provides
//    @Singleton
//    fun provideLoggingInterceptor(logger: Logger) = HttpLoggingInterceptor(
//        HttpLoggingInterceptor.Logger {
//            if (logger.isDebugEnabled) {
//                logger.debug(it)
//            }
//        })
//
//    @Provides
//    @Singleton
//    fun provideLogger() = LoggerFactory.getLogger(NetworkModule::class.java)
//
//    @Provides
//    @Singleton
//    fun provideOkhttpClient(logInterceptor: HttpLoggingInterceptor) {
//        logInterceptor.level = mLogLevel
//
//        OkHttpClient.Builder()
//            .addInterceptor(logInterceptor).build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideRxJava2CallAdapterFactory() =
//        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
//
//    @Provides
//    @Singleton
//    fun provideJacksonConverterFactory() = JacksonConverterFactory.create()
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(rxAdapter: RxJava2CallAdapterFactory, jacksonFactory: JacksonConverterFactory,
//                        okhttpclient: OkHttpClient) =
//        Retrofit.Builder().baseUrl(mBaseUrl)
//            .addCallAdapterFactory(rxAdapter)
//            .addConverterFactory(jacksonFactory)
//            .client(okhttpclient)
//            .build()
//}
