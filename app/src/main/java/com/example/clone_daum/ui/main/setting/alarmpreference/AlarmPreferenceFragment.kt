package com.example.clone_daum.ui.main.setting.alarmpreference

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.AlarmPreferenceFragmentBinding
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

class AlarmPreferenceFragment @Inject constructor(
): BaseDaggerFragment<AlarmPreferenceFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.alarm_preference_fragment

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            initAdapter(R.layout.setting_category_item,
                R.layout.setting_normal_item,
                R.layout.setting_color_item,
                R.layout.setting_switch_item,
                R.layout.setting_check_item,
                R.layout.setting_depth_item,
                R.layout.daum_app_info_item
            )

            title(R.string.setting_alarm_preference)
            alarmPreferenceSettingType()
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
        @ContributesAndroidInjector(modules = [AlarmPreferenceFragmentModule::class])
        abstract fun contributeAlarmPreferenceFragmentInjector(): AlarmPreferenceFragment
    }

    @dagger.Module
    abstract class AlarmPreferenceFragmentModule {
        @Binds
        abstract fun bindAlarmPreferenceFragment(fragment: AlarmPreferenceFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}