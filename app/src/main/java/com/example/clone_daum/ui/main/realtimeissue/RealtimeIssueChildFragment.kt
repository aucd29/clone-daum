package com.example.clone_daum.ui.main.realtimeissue

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.RealtimeIssueChildFragmentBinding
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.ui.Navigator
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 9. <p/>
 */

class RealtimeIssueChildFragment @Inject constructor()
    : BaseDaggerFragment<RealtimeIssueChildFragmentBinding, RealtimeIssueChildViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueChildFragment::class.java)
    }

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var navigator: Navigator

    override val layoutId = R.layout.realtime_issue_child_fragment

    private val mRealtimeIssueViewModel: RealtimeIssueViewModel by activityInject()

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

        navigator.browserFragment(url)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [RealtimeIssueChildFragmentModule::class])
        abstract fun contributeRealtimeIssueChildFragmentInjector(): RealtimeIssueChildFragment
    }

    @dagger.Module
    abstract class RealtimeIssueChildFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: RealtimeIssueChildFragment): SavedStateRegistryOwner
    }
}