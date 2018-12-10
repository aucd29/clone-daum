package com.example.clone_daum.di.module.common

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun provideContext(app: Application): Context

    // https://stackoverflow.com/questions/48081881/dagger-2-not-injecting-sharedpreference
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideSharedPreference(context: Context)
                = PreferenceManager.getDefaultSharedPreferences(context)
    }
}