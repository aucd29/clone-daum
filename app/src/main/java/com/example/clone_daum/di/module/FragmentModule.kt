package com.example.clone_daum.di.module

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.main.MainWebviewFragment
import com.example.clone_daum.ui.search.SearchFragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [MainFragment.Module::class
        , MainWebviewFragment.Module::class
        , SearchFragment.Module::class
])
class FragmentModule {
    @Singleton
    @Provides
    fun provideFragmentManager(activity: FragmentActivity) =
            activity.supportFragmentManager

    @Singleton
    @Provides
    fun provideChildFragmentManager(fragment: Fragment) =
            fragment.childFragmentManager
}