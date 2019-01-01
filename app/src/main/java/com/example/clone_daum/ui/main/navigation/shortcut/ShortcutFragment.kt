package com.example.clone_daum.ui.main.navigation.shortcut

import com.example.clone_daum.databinding.ShortcutFragmentBinding
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class ShortcutFragment: BaseDaggerFragment<ShortcutFragmentBinding, ShortcutViewModel>() {
    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): ShortcutFragment
    }
}
