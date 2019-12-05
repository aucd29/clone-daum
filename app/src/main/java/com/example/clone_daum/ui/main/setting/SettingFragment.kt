package com.example.clone_daum.ui.main.setting

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.SettingFragmentBinding
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class SettingFragment @Inject constructor(
): BaseDaggerFragment<SettingFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.setting_fragment

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
        @ContributesAndroidInjector(modules = [SettingFragmentModule::class])
        abstract fun contributeSettingFragmentInjector(): SettingFragment
    }

    @dagger.Module
    abstract class SettingFragmentModule {
        @Binds
        abstract fun bindSettingFragment(fragment: SettingFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}

