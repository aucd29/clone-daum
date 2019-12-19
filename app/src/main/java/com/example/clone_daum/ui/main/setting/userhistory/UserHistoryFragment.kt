package com.example.clone_daum.ui.main.setting.userhistory

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.UserHistoryFragmentBinding
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-18 <p/>
 */

class UserHistoryFragment @Inject constructor(
): BaseDaggerFragment<UserHistoryFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.user_history_fragment

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            title(R.string.setting_privacy_policy_remove_history)
            userHistorySettingType()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [UserHistoryFragmentModule::class])
        abstract fun contributeUserHistoryFragmentInjector(): UserHistoryFragment
    }

    @dagger.Module
    abstract class UserHistoryFragmentModule {
        @Binds
        abstract fun bindUserHistoryFragment(fragment: UserHistoryFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}