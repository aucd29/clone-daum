package com.example.clone_daum.ui.main.mediasearch

import com.example.clone_daum.databinding.MediaSearchFragmentBinding
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 16. <p/>
 */

class MediaSearchFragment : BaseDaggerFragment<MediaSearchFragmentBinding, MediaSearchViewModel>(){
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
        abstract fun contributeInjector(): MediaSearchFragment
    }
}