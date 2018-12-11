package com.example.clone_daum.di.module

import android.content.res.AssetManager
import androidx.fragment.app.FragmentManager
import com.example.clone_daum.di.module.common.AssetModule
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.ui.main.MainTabAdapter
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
    @Singleton
    @Provides
    fun provideDaumService(retrofit: Retrofit) =
        retrofit.create(DaumService::class.java)

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
