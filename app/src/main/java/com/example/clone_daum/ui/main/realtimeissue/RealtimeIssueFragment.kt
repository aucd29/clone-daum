package com.example.clone_daum.ui.main.realtimeissue

import com.example.clone_daum.databinding.RealtimeIssueFragmentBinding
import com.example.common.BaseDaggerBottomSheetDialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 9. <p/>
 */

class RealtimeIssueFragment
    : BaseDaggerBottomSheetDialogFragment<RealtimeIssueFragmentBinding, RealtimeIssueViewModel>() {
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
        abstract fun contributeInjector(): RealtimeIssueFragment
    }
}