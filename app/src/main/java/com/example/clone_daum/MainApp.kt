package com.example.clone_daum

import android.support.multidex.MultiDexApplication
import com.example.clone_daum.di.component.*
import com.example.clone_daum.di.module.DaumModule
import com.example.clone_daum.di.module.RoomModule
import com.example.clone_daum.di.module.common.AppModule
import com.example.clone_daum.di.module.common.NetworkModule
import com.example.clone_daum.model.Repository
import com.example.clone_daum.model.remote.DaumService
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 26. <p/>
 */

class MainApp : MultiDexApplication() {
    companion object {
        const val BASE_URL = "https://github.com/aucd29/"
    }

//    lateinit var app: AppComponent

    @Inject
    lateinit var dmService: DaumService

    override fun onCreate() {
        super.onCreate()

//        app = DaggerAppComponent.builder()
//            .appModule(AppModule(this))
//            .networkModule(NetworkModule(BASE_URL, HttpLoggingInterceptor.Level.BODY))
//            .daumModule(DaumModule())
//            .roomModule(RoomModule())
//            .build()
//
//        app.inject(this)
//
//        Repository.init(this)

//        dmService.popularKeywordList().
    }
}