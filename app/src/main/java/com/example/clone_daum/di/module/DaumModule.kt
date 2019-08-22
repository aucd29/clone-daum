package com.example.clone_daum.di.module

import brigitte.di.dagger.module.AssetModule
import brigitte.di.dagger.module.RxModule
import brigitte.di.dagger.module.ViewModelFactoryModule
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.DaumSuggestService
import com.example.clone_daum.model.remote.GithubService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [ ViewModelFactoryModule::class
    , ViewModelModule::class
    , NetworkModule::class
    , DbModule::class
    , AssetModule::class
    , RxModule::class
    , RxLocationModule::class
    , CalligraphyModule::class
])
class DaumModule {
    companion object {
        const val GITHUB_BASE_URL       = "https://raw.githubusercontent.com/"
        const val DAUM_BASE_URL         = "https://m.daum.net/"
        const val DAUM_SUGGEST_BASE_URL = "https://msuggest.search.daum.net/"
    }

    // 다수개의 retrofit 을 이용해야 하므로 Retrofit.Builder 를 전달 받은 후
    // 이곳에서 baseurl 을 설정하는 방식을 이용한다.

    @Singleton
    @Provides
    fun provideGithubService(retrofitBuilder: Retrofit.Builder): GithubService =
        retrofitBuilder.baseUrl(GITHUB_BASE_URL).build()
            .create(GithubService::class.java)

    @Singleton
    @Provides
    fun provideDaumService(retrofitBuilder: Retrofit.Builder): DaumService =
        retrofitBuilder.baseUrl(DAUM_BASE_URL).build()
            .create(DaumService::class.java)

    @Singleton
    @Provides
    fun provideDaumSuggestService(retrofitBuilder: Retrofit.Builder): DaumSuggestService =
        retrofitBuilder.baseUrl(DAUM_SUGGEST_BASE_URL).build()
            .create(DaumSuggestService::class.java)
}
