package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FolderFragmnetBinding
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 8. <p/>
 */

class FolderFragment : BaseDaggerFragment<FolderFragmnetBinding, FavoriteViewModel>() {
    override fun initViewBinding() {

    }

    override fun initViewModelEvents() {
        mViewModel.initFolder(mDisposable)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FolderFragment
    }
}