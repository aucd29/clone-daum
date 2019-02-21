package com.example.clone_daum.ui.main.realtimeissue

import com.example.clone_daum.databinding.RealtimeIssueChildFragmentBinding
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import com.example.common.di.module.injectOfActivity
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 9. <p/>
 */

class RealtimeIssueChildFragment
    : BaseDaggerFragment<RealtimeIssueChildFragmentBinding, RealtimeIssueChildViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueChildFragment::class.java)
    }

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var viewController: ViewController

    lateinit var mRealtimeIssueViewModel: RealtimeIssueViewModel

    override fun bindViewModel() {
        super.bindViewModel()

        mRealtimeIssueViewModel = mViewModelFactory.injectOfActivity(this, RealtimeIssueViewModel::class.java)
    }

    override fun initViewBinding() {

    }

    override fun initViewModelEvents() {
        arguments?.getInt("position")?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("POSITION : $it")
            }

            // main 에서 load 한 데이터를 읽어다가 출력
            mRealtimeIssueViewModel.issueList(it)?.let {
                mViewModel.initAdapter(it)
            }
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            RealtimeIssueChildViewModel.CMD_BRS_OPEN -> data.let {
                mRealtimeIssueViewModel.finishEvent()

                viewController.browserFragment(it.toString())
            }
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