package com.example.clone_daum.di.module

import com.example.clone_daum.di.module.common.AssetModule
import com.example.clone_daum.model.remote.DaumService
import com.example.common.di.module.RxModule
import com.example.clone_daum.model.remote.DaumSuggestService
import com.example.clone_daum.model.remote.GithubService
import com.example.common.di.module.ViewModelFactoryModule
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [ViewModelFactoryModule::class
    , ViewModelModule::class
    , NetworkModule::class
    , DbModule::class
    , AssetModule::class
    , ConfigModule::class
    , RxModule::class
    , CalligraphyModule::class
])
class DaumModule {
    companion object {
        val GITHUB_BASE_URL       = "https://raw.githubusercontent.com/"
        val DAUM_BASE_URL         = "https://m.daum.net/"
        val DAUM_SUGGEST_BASE_URL = "https://msuggest.search.daum.net/"
    }

    // 다수개의 retrofit 을 이용해야 하므로 Retrofit.Builder 를 전달 받은 후
    // 이곳에서 baseurl 을 설정하는 방식을 이용한다.

    @Singleton
    @Provides
    fun provideGithubService(retrofitBuilder: Retrofit.Builder) =
        retrofitBuilder.baseUrl(GITHUB_BASE_URL).build()
            .create(GithubService::class.java)

    @Singleton
    @Provides
    fun provideDaumService(retrofitBuilder: Retrofit.Builder) =
        retrofitBuilder.baseUrl(DAUM_BASE_URL).build()
            .create(DaumService::class.java)

    @Singleton
    @Provides
    fun provideDaumSuggestService(retrofitBuilder: Retrofit.Builder) =
        retrofitBuilder.baseUrl(DAUM_SUGGEST_BASE_URL).build()
            .create(DaumSuggestService::class.java)
}
