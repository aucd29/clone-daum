package com.example.clone_daum.ui.main

import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainFragment : BaseDaggerFragment<MainFragmentBinding, MainViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() {

    }

    override fun initViewModelEvents() = mViewModel.run {
        // fixme main tab adapter 이건 고민 해봐야 될 듯 -_-;
        tabAdapter.set(MainTabAdapter(childFragmentManager, preConfig.tabLabelList))
        viewpager.set(mBinding.viewpager)

        // n 사도 그렇지만 k 사도 search 쪽을 view 로 가려서 하는 데
        // -_ - 이러한 구조를 가져가는게
        // 딱히 득이 될건 없어 보이는데 흠 ; 전국적으로 헤더 만큼에 패킷 낭비가...
        appbarOffsetChangedEvent.set { appbar, offset ->
            val maxScroll = appbar.getTotalScrollRange()
            val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

            if (mLog.isTraceEnabled) {
                mLog.trace("APP BAR (ALPHA) : $percentage")
            }

            mBinding.searchArea.alpha   = 1.0f - percentage
            appbarOffsetLiveEvent.value = offset
        }
    }

    override fun onCommandEvent(cmd: String, obj: Any?) = MainViewModel.run {
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND EVENT : $cmd")
        }

        when (cmd) {
            CMD_SEARCH_FRAMGNET         -> viewController.searchFragment()
            CMD_NAVIGATION_FRAGMENT     -> viewController.navigationFragment()
            CMD_REALTIME_ISSUE_FRAGMENT -> viewController.realtimeIssueFragment()
            CMD_WEATHER_FRAGMENT        -> viewController.weatherFragment()
            CMD_BRS_OPEN                -> obj?.let { viewController.browserFragment(it.toString()) } ?: Unit
        }
    }

    override fun onPause() {
        mViewModel.stopRealtimeIssue()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        mViewModel.startRealtimeIssue()
    }

    override fun onDestroy() {
        mBinding.viewpager.adapter = null

        super.onDestroy()
    }
    
    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @dagger.android.ContributesAndroidInjector
        abstract fun contributeInjector(): MainFragment
    }
}


