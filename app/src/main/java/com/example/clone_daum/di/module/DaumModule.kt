package com.example.clone_daum.di.module

import android.content.res.AssetManager
import com.example.clone_daum.di.module.common.AssetModule
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.GithubService
import com.example.common.jsonParse
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [NetworkModule::class
    , DbModule::class
    , AssetModule::class
    , ChipModule::class])
class DaumModule {
    companion object {
        val GITHUB_BASE_URL = "https://raw.githubusercontent.com/"
        val DAUM_BASE_URL   = "https://msuggest.search.daum.net/"
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
    fun provideTabList(assetManager: AssetManager) =
        Observable.just(assetManager.open("res/tab.json").readBytes())
            .observeOn(Schedulers.computation())
            .map { it.jsonParse<List<TabData>>() }
            .blockingFirst()

//    @Singleton
//    @Provides
//    fun provideMainTabAdapter(frgmtManager: FragmentManager, tabListData: List<TabData>) =
//        MainTabAdapter(frgmtManager, tabListData)

//    @Singleton
//    @Provides
//    fun provideMainTabAdapter(frgmtManager: FragmentManager, tabListData: Observable<ByteArray>): MainTabAdapter {
//        return MainTabAdapter(frgmtManager, tabListData.map { it.jsonParse<List<TabData>>() }.blockingFirst())
//    }
}
