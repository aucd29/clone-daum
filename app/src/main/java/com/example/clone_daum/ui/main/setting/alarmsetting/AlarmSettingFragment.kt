package com.example.clone_daum.ui.main.setting.alarmsetting

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.AlarmSettingFragmentBinding
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-17 <p/>
 */

class AlarmSettingFragment @Inject constructor(
): BaseDaggerFragment<AlarmSettingFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.alarm_setting_fragment

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            title(R.string.setting_alarm_item)
            alarmSettingType()
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
        @ContributesAndroidInjector(modules = [AlarmSettingFragmentModule::class])
        abstract fun contributeAlarmSettingFragmentInjector(): AlarmSettingFragment
    }

    @dagger.Module
    abstract class AlarmSettingFragmentModule {
        @Binds
        abstract fun bindAlarmSettingFragment(fragment: AlarmSettingFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}