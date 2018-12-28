package com.example.clone_daum.ui.main.navigation.cafe

import com.example.clone_daum.databinding.CafeFragmentBinding
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class CafeFragment: BaseDaggerFragment<CafeFragmentBinding, CafeViewModel>() {
    override fun settingEvents() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): CafeFragment
    }
}