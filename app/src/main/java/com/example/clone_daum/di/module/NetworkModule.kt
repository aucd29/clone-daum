package com.example.clone_daum.di.module

import brigitte.AuthorizationInterceptor
import brigitte.di.dagger.module.OkhttpModule
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.DaumSuggestService
import com.example.clone_daum.model.remote.GithubService
import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [OkhttpModule::class])
class NetworkModule {
    // 다수개의 retrofit 을 이용해야 하므로 Retrofit.Builder 를 전달 받은 후
    // 이곳에서 baseurl 을 설정하는 방식을 이용한다.

    companion object {
        val LOG_CLASS = NetworkModule::class.java

        const val GITHUB_BASE_URL       = "https://raw.githubusercontent.com/"
        const val DAUM_BASE_URL         = "https://m.daum.net/"
        const val DAUM_SUGGEST_BASE_URL = "https://msuggest.search.daum.net/"
    }

    @Provides
    @Singleton
    fun provideGithubService(retrofitBuilder: Retrofit.Builder): GithubService =
        retrofitBuilder.baseUrl(GITHUB_BASE_URL).build()
            .create(GithubService::class.java)

    @Provides
    @Singleton
    fun provideDaumService(retrofitBuilder: Retrofit.Builder): DaumService =
        retrofitBuilder.baseUrl(DAUM_BASE_URL).build()
            .create(DaumService::class.java)

    @Provides
    @Singleton
    fun provideDaumSuggestService(retrofitBuilder: Retrofit.Builder): DaumSuggestService =
        retrofitBuilder.baseUrl(DAUM_SUGGEST_BASE_URL).build()
            .create(DaumSuggestService::class.java)

    @Provides
    @Singleton
    fun provideLogger(): Logger =
        LoggerFactory.getLogger(LOG_CLASS)

    @Provides
    @Singleton
    fun provideLogLevel() =
        HttpLoggingInterceptor.Level.BODY

    @Provides
    @Singleton
    fun provideAuthorizationInterceptor(): AuthorizationInterceptor? = null
}
