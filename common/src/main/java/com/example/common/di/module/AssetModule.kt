package com.example.clone_daum.di.module.common

import android.content.Context
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 10. <p/>
 */

@Module
class AssetModule {
    @Singleton
    @Provides
    fun provideAssetManager(context: Context) = context.assets
}