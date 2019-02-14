package com.example.clone_daum.ui.main

import android.Manifest
import android.view.View
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import com.example.clone_daum.ui.main.weather.WeatherViewModel
import com.example.clone_daum.ui.search.PopularViewModel
import com.example.common.*
import com.example.common.di.module.injectOfActivity
import com.example.common.runtimepermission.PermissionParams
import com.example.common.runtimepermission.runtimePermissions
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject


class MainFragment : BaseDaggerFragment<MainFragmentBinding, MainViewModel>()
    , TabLayout.OnTabSelectedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)
    }

    init {
        // MainViewModel 를 MainWebViewFragment 와 공유
        mViewModelScope = SCOPE_ACTIVITY
    }

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var disposable: CompositeDisposable

    private lateinit var mWeatherViewModel: WeatherViewModel
    private lateinit var mRealtimeIssueViewModel : RealtimeIssueViewModel
    private lateinit var mPopularViewModel: PopularViewModel

    override fun bindViewModel() {
        super.bindViewModel()

        initWeatherViewModel()
        initRealtimeIssueViewModel()
        initPopularViewModel()

        disposable.add(preConfig.daumMain()
            .subscribe({ html ->
                mRealtimeIssueViewModel.load(html)
                mPopularViewModel.load(html)
            }, { e ->
                if (mLog.isDebugEnabled) {
                    e.printStackTrace()
                }

                mLog.error("ERROR: ${e.message}")
            }))
    }

    private fun initWeatherViewModel() {
        mWeatherViewModel     = mViewModelFactory.injectOfActivity(this, WeatherViewModel::class.java)
        mBinding.weatherModel = mWeatherViewModel
    }

    private fun initRealtimeIssueViewModel() {
        mRealtimeIssueViewModel     = mViewModelFactory.injectOfActivity(this, RealtimeIssueViewModel::class.java)
        mBinding.realtimeIssueModel = mRealtimeIssueViewModel
    }

    private fun initPopularViewModel() {
        mPopularViewModel = mViewModelFactory.injectOfActivity(this, PopularViewModel::class.java)
    }

    override fun initViewBinding() {
        mBinding.apply {
            searchBar.globalLayoutListener {
                if (mLog.isDebugEnabled) {
                    mLog.debug("APP BAR HEIGHT : ${searchBar.height}")
                }

                mViewModel.progressViewOffsetLive.value = searchBar.height
            }

            tab.apply {
                addOnTabSelectedListener(this@MainFragment)

                // 1번째 tab 을 focus 해야 됨 (news)
                postDelayed({ getTabAt(1)?.select() }, 100)
            }
        }
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
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

                // scroll 되어 offset 된 값을 webview 쪽으로 전달
                appbarOffsetLive.value    = offset
            }
        }

        observe(mRealtimeIssueViewModel.commandEvent) {
            onCommandEvent(it.first, it.second)
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        MainViewModel.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("COMMAND EVENT : $cmd")
            }

            // NAVIGATION EDITOR 로 변경해야 되나? -_ -ㅋ
            viewController.apply {
                when (cmd) {
                    CMD_SEARCH_FRAMGNET         -> searchFragment()
                    CMD_NAVIGATION_FRAGMENT     -> navigationFragment()
                    CMD_REALTIME_ISSUE_FRAGMENT -> mRealtimeIssueViewModel.mRealtimeIssueList?.let {
                        if (it.size > 0) {
                            realtimeIssueFragment()
                        } else {
//                            alert(R.string.main_realtime_issue_load_error, R.string.error_title)
                        }
                    }
                    CMD_WEATHER_FRAGMENT        -> weatherFragment()
                    CMD_MEDIA_SEARCH_FRAGMENT   -> mediaSearchFragment()
                    CMD_BRS_OPEN                -> browserFragment(data.toString())
                    CMD_PERMISSION_GPS          -> runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                        , { req, res ->
                            if (mLog.isDebugEnabled) {
                                mLog.debug("PERMISSION LOCATION : $res")
                            }

                            if (res) {
                                mViewModel.visibleGps.set(View.GONE)
                                mWeatherViewModel.refreshCurrentLocation()
                            }
                        }))
                }
            }
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
        mBinding.apply {
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

        mViewModel.currentTabPositionLive.value = tab.position
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


