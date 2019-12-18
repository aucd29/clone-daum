package com.example.clone_daum.ui.main.setting.privacypolicy

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.PrivacyPolicyFragmentBinding
import com.example.clone_daum.model.local.SettingType
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-17 <p/>
 */

class PrivacyPolicyFragment @Inject constructor(
): BaseDaggerFragment<PrivacyPolicyFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.privacy_policy_fragment

    private var mSettingType: List<SettingType>? = null

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.title(R.string.setting_privacy)
        mSettingType = mViewModel.privacyPolicySettingType()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [PrivacyPolicyFragmentModule::class])
        abstract fun contributePrivacyPolicyFragmentInjector(): PrivacyPolicyFragment
    }

    @dagger.Module
    abstract class PrivacyPolicyFragmentModule {
        @Binds
        abstract fun bindPrivacyPolicyFragment(fragment: PrivacyPolicyFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}