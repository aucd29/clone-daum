package com.example.clone_daum.model.remote

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 3. <p/>
 */

interface DaumService {
    @GET("/clone-daum/tree/merge_search/dumy/popular-keyword.json")
    fun popularKeywordList(): Observable<List<String>>
}

object Http {
    private val mLog = LoggerFactory.getLogger(Http::class.java)

    val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
        if (mLog.isDebugEnabled) {
            mLog.debug(it)
        }
    })

    const val URL = "http://github.com/aucd29"

    val http = Retrofit.Builder().baseUrl(URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(JacksonConverterFactory.create())
        .client(OkHttpClient.Builder().addInterceptor(logger).build())
        .build()

    init {
        logger.level = HttpLoggingInterceptor.Level.BODY
    }

    fun popularKeywordList() = http.create(DaumService::class.java)
        .popularKeywordList()
}