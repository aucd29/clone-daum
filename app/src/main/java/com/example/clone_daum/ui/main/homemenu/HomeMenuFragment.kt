package com.example.clone_daum.ui.main.homemenu

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.HomeMenuFragmentBinding
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class HomeMenuFragment @Inject constructor(
): BaseDaggerFragment<HomeMenuFragmentBinding, HomeMenuViewModel>() {
    override val layoutId = R.layout.home_menu_fragment

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [HomeMenuFragmentModule::class])
        abstract fun contributeHomeMenuFragmentInjector(): HomeMenuFragment
    }

    @dagger.Module
    abstract class HomeMenuFragmentModule {
        @Binds
        abstract fun bindHomeMenuFragment(fragment: HomeMenuFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}