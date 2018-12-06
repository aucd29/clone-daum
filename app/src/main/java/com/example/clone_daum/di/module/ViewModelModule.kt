package com.example.clone_daum.di.module

import android.arch.lifecycle.ViewModelProviders
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.main.MainViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun provideMainViewModel(frgmt: MainFragment) =
        ViewModelProviders.of(frgmt.activity!!).get(MainViewModel::class.java)
}