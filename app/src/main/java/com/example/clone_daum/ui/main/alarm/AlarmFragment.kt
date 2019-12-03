package com.example.clone_daum.ui.main.alarm

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.AlarmFragmentBinding
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class AlarmFragment @Inject constructor(
): BaseDaggerFragment<AlarmFragmentBinding, AlarmViewModel>() {
    override val layoutId = R.layout.alarm_fragment

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
        @ContributesAndroidInjector(modules = [AlarmFragmentModule::class])
        abstract fun contributeAlarmFragmentInjector(): AlarmFragment
    }

    @dagger.Module
    abstract class AlarmFragmentModule {
        @Binds
        abstract fun bindAlarmFragment(fragment: AlarmFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}