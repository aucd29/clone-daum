package com.example.clone_daum.ui.main.navigation

import com.example.clone_daum.databinding.NavigationFragmentBinding
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 20. <p/>
 */

class NavigationFragment: BaseDaggerFragment<NavigationFragmentBinding, NavigationViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationFragment::class.java)
    }

    override fun settingEvents() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): NavigationFragment
    }
}