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

class RealtimeIssueChildFragment @Inject constructor(
) : BaseDaggerFragment<RealtimeIssueChildFragmentBinding, RealtimeIssueChildViewModel>() {
    override val layoutId = R.layout.realtime_issue_child_fragment
    companion object {
        private val logger = LoggerFactory.getLogger(RealtimeIssueChildFragment::class.java)
    }

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var navigator: Navigator

    private val realtimeIssueViewModel: RealtimeIssueViewModel by activityInject()
    private val position: Int
        get() = arguments?.getInt(RealtimeIssueTabAdapter.K_POS)!!

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        if (logger.isDebugEnabled) {
            logger.debug("POSITION : $position")
        }

        // main 에서 load 한 데이터를 읽어다가 출력
        realtimeIssueViewModel.issueList(position)?.let { list ->
            viewModel.apply {
                initAdapter(R.layout.realtime_issue_child_item)
                initRealtimeIssueAdapter(list)
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
        realtimeIssueViewModel.command(RealtimeIssueViewModel.CMD_CLOSE_ISSUE)

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