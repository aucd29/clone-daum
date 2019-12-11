package com.example.clone_daum.ui.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import brigitte.BaseDaggerFragment
import brigitte.color
import brigitte.di.dagger.scope.FragmentScope
import brigitte.toast
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

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

class SettingDetailFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting_root)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        toast("preference: ${preference?.key}")

        return super.onPreferenceTreeClick(preference)
    }
}