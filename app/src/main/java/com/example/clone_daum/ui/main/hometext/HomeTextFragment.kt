package com.example.clone_daum.ui.main.hometext

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerDialogFragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.HomeTextFragmentBinding
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class HomeTextFragment @Inject constructor(
): BaseDaggerDialogFragment<HomeTextFragmentBinding, HomeTextViewModel>() {
    override val layoutId = R.layout.home_text_fragment

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
        @ContributesAndroidInjector(modules = [HomeTextFragmentModule::class])
        abstract fun contributeHomeTextFragmentInjector(): HomeTextFragment
    }

    @dagger.Module
    abstract class HomeTextFragmentModule {
        @Binds
        abstract fun bindHomeTextFragment(fragment: HomeTextFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}
