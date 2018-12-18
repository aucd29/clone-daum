package com.example.clone_daum.ui.browser

import com.example.clone_daum.databinding.BrowserSubmenuFragmentBinding
import com.example.common.BaseDaggerFragment
import com.example.common.app
import com.example.common.bindingadapter.AnimParams
import com.example.common.dpToPx
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

class BrowserSubmenuFragment : BaseDaggerFragment<BrowserSubmenuFragmentBinding, BrowserSubmenuViewModel>() {
    override fun settingEvents() = mViewModel.run {
        submenuLayoutAni.set(AnimParams(value = 0f, initValue = 200.dpToPx(app),
            duration = 600))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserSubmenuFragment
    }
}
