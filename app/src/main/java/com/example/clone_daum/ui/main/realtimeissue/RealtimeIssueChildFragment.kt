package com.example.clone_daum.ui.main.realtimeissue

import com.example.clone_daum.databinding.RealtimeIssueChildFragmentBinding
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.ui.ViewController
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.module.injectOfActivity
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

    // main fragment 와 공유
    lateinit var mRealtimeIssueViewModel: RealtimeIssueViewModel

    override fun bindViewModel() {
        super.bindViewModel()

        mRealtimeIssueViewModel = mViewModelFactory.injectOfActivity(this)
    }

    override fun initViewBinding() {

    }

    override fun initViewModelEvents() {
        arguments?.getInt("position")?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("POSITION : $it")
            }

            // main 에서 load 한 데이터를 읽어다가 출력
            mRealtimeIssueViewModel.issueList(it)?.let { list ->
                mViewModel.initRealtimeIssueAdapter(list)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            RealtimeIssueChildViewModel.CMD_BRS_OPEN -> showBrowser(data.toString())
        }
    }

    private fun showBrowser(url: String) {
        mRealtimeIssueViewModel.command(RealtimeIssueViewModel.CMD_CLOSE_ISSUE)

        viewController.browserFragment(url)
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