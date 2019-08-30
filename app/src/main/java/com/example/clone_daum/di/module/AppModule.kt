package com.example.clone_daum.di.module

import android.app.Application
import brigitte.di.dagger.module.ContextModule
import com.example.clone_daum.MainApp
import dagger.Module
import dagger.Provides

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-30 <p/>
 */

// lib 단에서 context 를 이용하기 위해 ContextModule 은 하위로 내림
@Module(includes = [ContextModule::class])
class AppModule {
    @Provides
    fun provideApplication(app: MainApp): Application =
        app
}