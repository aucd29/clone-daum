package com.example.clone_daum.ui.main.setting.research

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.ResearchFragmentBinding
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-17 <p/>
 */

class ResearchFragment @Inject constructor(
): BaseDaggerFragment<ResearchFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.research_fragment

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            title(R.string.setting_research)
            researchSettingType()
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
        @ContributesAndroidInjector(modules = [ResearchFragmentModule::class])
        abstract fun contributeResearchFragmentInjector(): ResearchFragment
    }

    @dagger.Module
    abstract class ResearchFragmentModule {
        @Binds
        abstract fun bindResearchFragment(fragment: ResearchFragment): Fragment
    }
}