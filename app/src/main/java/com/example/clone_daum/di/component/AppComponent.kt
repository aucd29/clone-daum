package com.example.clone_daum.di.component

import android.content.SharedPreferences
import com.example.clone_daum.MainActivity
import com.example.clone_daum.MainApp
import com.example.clone_daum.di.module.DaumModule
import com.example.clone_daum.di.module.RoomModule
import com.example.clone_daum.di.module.ViewModelFactoryModule
import com.example.clone_daum.di.module.ViewModelModule
import com.example.clone_daum.di.module.common.AppModule
import com.example.clone_daum.di.module.common.NetworkModule
import com.example.clone_daum.model.local.LocalRepository
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.main.MainViewModel
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

// androidx 미 지원으로 인해 나중에 코딩하자.. =_ =;;
//
//@Singleton
//@Component(modules = arrayOf(AppModule::class
//    , NetworkModule::class
//    , DaumModule::class
//    , RoomModule::class
////    , ViewModelFactoryModule::class
////    , ViewModelModule::class
//))
//interface AppComponent {
//    fun preference(): SharedPreferences
//    fun retrofit(): Retrofit
//    fun daum(): DaumService
//    fun localRepository(): LocalRepository
//
////    fun viewModelProvider(): ViewModelProvider
////    fun mainViewModel(): MainViewModel
//
//    fun inject(app: MainApp)
//    fun inject(act: MainActivity)
//    fun inject(frgmt: MainFragment)
//}