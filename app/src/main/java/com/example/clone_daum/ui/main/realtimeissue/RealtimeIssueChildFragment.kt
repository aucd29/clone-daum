package com.example.clone_daum.ui.main.realtimeissue

import androidx.lifecycle.ViewModelProviders
import com.example.clone_daum.databinding.RealtimeIssueChildFragmentBinding
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 9. <p/>
 */

class RealtimeIssueChildFragment
    : BaseDaggerFragment<RealtimeIssueChildFragmentBinding, RealtimeIssueViewModel>() {

    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueChildFragment::class.java)
    }

    @Inject lateinit var preConfig: PreloadConfig

    override fun initViewBinding() {
        if (mLog.isDebugEnabled) {
            mLog.debug("BINDING CHILD FRAGMENT")
        }
    }

    override fun initViewModelEvents() {
        arguments?.getInt("position")?.let {
            mViewModel.init(it)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): RealtimeIssueChildFragment
    }
}