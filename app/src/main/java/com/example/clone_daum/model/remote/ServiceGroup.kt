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
    // https://raw.githubusercontent.com/aucd29/clone-daum/merge_search/dumy/popular-keyword.json
    @GET("/aucd29/clone-daum/merge_search/dumy/popular-keyword.json")
    fun popularKeywordList(): Observable<List<String>>
}