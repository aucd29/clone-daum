package com.example.clone_daum.di.module

import android.arch.lifecycle.ViewModelProviders
import com.example.clone_daum.MainActivity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 *
 * https://medium.com/@marco_cattaneo/android-viewmodel-and-factoryprovider-good-way-to-manage-it-with-dagger-2-d9e20a07084c
 */

@Module
class ViewModelFactoryModule {
    @Provides
    @Singleton
    fun provideViewModelProviders(act: MainActivity) =
        ViewModelProviders.of(act)
}