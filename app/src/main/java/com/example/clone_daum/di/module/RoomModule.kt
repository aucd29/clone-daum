package com.example.clone_daum.di.module

import android.content.Context
import com.example.clone_daum.model.local.LocalRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */


@Module
class RoomModule {
    @Provides
    @Singleton
    fun provideLocalRepository(context: Context) =
        LocalRepository.get(context)
}