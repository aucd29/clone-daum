package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FavoriteAddFragmentBinding
import com.example.common.BaseDaggerFragment
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */
class FavoriteAddFragment
    : BaseDaggerFragment<FavoriteAddFragmentBinding, FavoriteViewModel>() {
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
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FavoriteAddFragment
    }
}