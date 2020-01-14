package com.example.clone_daum.ui.main.setting.alarmsetting

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.AlarmSettingFragmentBinding
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
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
        viewModel.apply {
            initAdapter(R.layout.setting_category_item,
                R.layout.setting_normal_item,
                R.layout.setting_color_item,
                R.layout.setting_switch_item,
                R.layout.setting_check_item,
                R.layout.setting_depth_item,
                R.layout.daum_app_info_item
            )

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