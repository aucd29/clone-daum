package com.example.clone_daum.ui.main

import android.Manifest
import android.view.View
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import com.example.clone_daum.ui.main.weather.WeatherViewModel
import com.example.common.*
import com.example.common.di.module.injectOfActivity
import com.example.common.runtimepermission.PermissionParams
import com.example.common.runtimepermission.runtimePermissions
import com.google.android.material.tabs.TabLayout
import org.slf4j.LoggerFactory
import javax.inject.Inject


class MainFragment : BaseDaggerFragment<MainFragmentBinding, MainViewModel>()
    , TabLayout.OnTabSelectedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)

        private val REQ_RUNTIME_PERMISSION = 79
    }

    @Inject lateinit var viewController: ViewController

    private lateinit var mWeatherViewModel: WeatherViewModel
    private lateinit var mRealtimeIssueViewModel : RealtimeIssueViewModel

    override fun bindViewModel() {
        super.bindViewModel()

        mWeatherViewModel     = mViewModelFactory.injectOfActivity(this, WeatherViewModel::class.java)
        mBinding.weatherModel = mWeatherViewModel

        mRealtimeIssueViewModel = mViewModelFactory.injectOfActivity(this, RealtimeIssueViewModel::class.java)
        mRealtimeIssueViewModel.load()
        mBinding.realtimeIssueModel = mRealtimeIssueViewModel
    }

    override fun initViewBinding() = mBinding.run {
        searchBar.layoutListener {
            if (mLog.isDebugEnabled) {
                mLog.debug("APP BAR HEIGHT : ${searchBar.height}")
            }

            mViewModel.progressViewOffsetLive.value = searchBar.height
        }

        tab.addOnTabSelectedListener(this@MainFragment)
    }

    override fun initViewModelEvents() {
        mViewModel.run {
            // fixme main tab adapter 이건 고민 해봐야 될 듯 -_-;
            tabAdapter.set(MainTabAdapter(childFragmentManager, preConfig.tabLabelList))
            viewpager.set(mBinding.viewpager)

            // n 사도 그렇지만 k 사도 search 쪽을 view 로 가려서 하는 데
            // -_ - 이러한 구조를 가져가는게
            // 딱히 득이 될건 없어 보이는데 흠; 전국적으로 헤더 만큼에 패킷 낭비가...
            appbarOffsetChangedLive.set { appbar, offset ->
                val maxScroll = appbar.getTotalScrollRange()
                val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

                if (mLog.isTraceEnabled) {
                    mLog.trace("APP BAR (ALPHA) : $percentage")
                }

                mBinding.searchArea.alpha = 1.0f - percentage
                appbarOffsetLive.value    = offset
            }
        }

        observe(mRealtimeIssueViewModel.commandEvent) {
            onCommandEvent(it.first, it.second)
        }
    }

    override fun onCommandEvent(cmd: String, obj: Any?) = MainViewModel.run {
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND EVENT : $cmd")
        }

        // NAVIGATION EDITOR 로 변경해야 되나? -_ -ㅋ
        when (cmd) {
            CMD_SEARCH_FRAMGNET         -> viewController.searchFragment()
            CMD_NAVIGATION_FRAGMENT     -> viewController.navigationFragment()
            CMD_REALTIME_ISSUE_FRAGMENT -> viewController.realtimeIssueFragment()
            CMD_WEATHER_FRAGMENT        -> viewController.weatherFragment()
            CMD_MEDIA_SEARCH_FRAGMENT   -> viewController.mediaSearchFragment()
            CMD_BRS_OPEN                -> viewController.browserFragment(obj?.toString())
            CMD_PERMISSION_GPS          -> runtimePermissions(PermissionParams(activity()
                , arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                , { req, res ->
                    when (res) {
                        true -> {
                            mViewModel.run {
                                visibleGps.set(View.GONE)
                                mWeatherViewModel.refreshCurrentLocation()
                            }
                        }
                    }
                }, REQ_RUNTIME_PERMISSION))
        }
    }

    override fun onPause() {
        mRealtimeIssueViewModel.stopRealtimeIssue()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        mRealtimeIssueViewModel.startRealtimeIssue()
    }

    override fun onDestroy() {
        mRealtimeIssueViewModel.dp.dispose()
        mBinding.run {
            tab.removeOnTabSelectedListener(this@MainFragment)
            viewpager.adapter = null
        }

        super.onDestroy()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // TabLayout.OnTabSelectedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onTabReselected(tab: TabLayout.Tab) { }
    override fun onTabUnselected(tab: TabLayout.Tab) { }
    override fun onTabSelected(tab: TabLayout.Tab) {
        if (mLog.isDebugEnabled) {
            mLog.debug("TAB SELECTED ${tab.position}")
        }

        mViewModel.selectedTabPosition = tab.position
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


