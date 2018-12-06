package com.example.clone_daum.di.module.common

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module
class AppModule(private val mApp: Application) {
    @Provides
    @Singleton
    fun provideApplication() = mApp

    @Provides
    @Singleton
    fun provideContext() = mApp.applicationContext

    @Provides
    @Singleton
    fun provideSharedPreference(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context)
}