package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueTabAdapter
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import com.example.clone_daum.ui.search.PopularViewModel
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.di.dagger.module.injectOfActivity
import com.google.android.material.tabs.TabLayout
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import com.example.clone_daum.common.Config
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import kotlin.math.abs


// 어라라.. 머지가 잘못되었나? 왜 검색 영역에서 스크롤이 되는것이냐? ㄷ ㄷ ㄷ [aucd29][2019. 4. 17.]

class MainFragment @Inject constructor() : BaseDaggerFragment<MainFragmentBinding, MainViewModel>()
    , TabLayout.OnTabSelectedListener, OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)
    }

    init {
        mViewModelScope = SCOPE_ACTIVITY        // MainViewModel 를 MainWebViewFragment 와 공유
    }

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var mainTabAdapter: MainTabAdapter
    @Inject lateinit var realtimeIssueTabAdapter: dagger.Lazy<RealtimeIssueTabAdapter>

    private lateinit var mRealtimeIssueViewModel : RealtimeIssueViewModel
    private lateinit var mPopularViewModel: PopularViewModel    // SearchFragment 와 공유

    private var mCurrentTabPos: Int = 0
    private val mRealtimeTabSelectedListener = object: TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) { }
        override fun onTabUnselected(p0: TabLayout.Tab?) { }
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let { customRealtimeIssueTabText(it.position) }
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()

        initRealtimeIssueViewModel()
        initPopularViewModel()

        mDisposable.add(preConfig.daumMain()
            .subscribe({ html ->
                mRealtimeIssueViewModel.load(html)
                mPopularViewModel.load(html, mDisposable)
            }, { errorLog(it, mLog) }))
    }

    private fun initRealtimeIssueViewModel() {
        mRealtimeIssueViewModel     = mViewModelFactory.injectOfActivity(this)
        mBinding.realtimeIssueModel = mRealtimeIssueViewModel
    }

    private fun initPopularViewModel() {
        mPopularViewModel = mViewModelFactory.injectOfActivity(this)
    }

    override fun initViewBinding() {
        mBinding.apply {
            tab.apply {
                addOnTabSelectedListener(this@MainFragment)

                // 1번째 tab 을 focus 해야 됨 (news)
                postDelayed({ getTabAt(1)?.select() }, 100)
            }

            searchBar.globalLayoutListener {
                val result = realtimeIssueAreaMargin()

                if (mLog.isDebugEnabled) {
                    mLog.debug("APP BAR HEIGHT : ${searchBar.height}")
                }

                mViewModel.progressViewOffsetLive.value = searchBar.height
                mViewModel.searchAreaHeight             = searchBar.height - tabLayout.height

                result
            }

            realtimeIssueViewpager.let {
                it.globalLayoutListener {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("REALTIME ISSUE VIEWPAGER HEIGHT : ${it.height}")
                    }

                    it.translationY = it.height * -1f
                    it.translationY != 0f
                }
            }

            rootContainer.dispatchTouchEvent = { mViewModel.run {
                val y   = appbarOffsetLive.value!!
                val max = searchAreaHeight * -1
                val mid = max / 2

                when (it.action) {
                    MotionEvent.ACTION_UP -> {
                        val result = when (y) {
                            in mid..-1 -> {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("MAGNETIC EFFECT SCROLL UP : $y")
                                }

                                searchBar.setExpanded(true, true)

                                true
                            }
                            in (max + 1) until mid -> {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("MAGNETIC EFFECT SCROLL DOWN : $y")
                                }

                                searchBar.setExpanded(false, true)

                                true
                            }
                            else -> false
                        }

                        if (mLog.isTraceEnabled) {
                            mLog.trace("DISPATCH TOUCH EVENT RESULT : $result")
                        }

                        return@run result
                    }
                }

                false
            } }
        }
    }

    private fun realtimeIssueAreaMargin(): Boolean {
        mBinding.apply {
            realtimeIssueArea.apply {
                // 검색쪽 위치까지 margin 이동
                (layoutParams as ConstraintLayout.LayoutParams).let {
                    it.topMargin = searchArea.height - searchUnderline.height

                    if (mLog.isDebugEnabled) {
                        mLog.info("REALTIME ISSUE AREA TOP MARGIN : ${it.topMargin}")
                    }

                    it.height    = 1    // 0 의 경우 제대로 동작하지 않는 문제?
                    layoutParams = it

                    return it.topMargin != 0
                }
            }
        }
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            // fixme main tab adapter 이건 고민 해봐야 될 듯 -_-;
//            tabAdapter.set(MainTabAdapter(childFragmentManager, preConfig))
            tabAdapter.set(mainTabAdapter)
            viewpager.set(mBinding.viewpager)

            // n 사도 그렇지만 k 사도 search 쪽을 view 로 가려서 하는 데
            // -_ - 이러한 구조를 가져가는게
            // 딱히 득이 될건 없어 보이는데 흠; 전국적으로 헤더 만큼에 패킷 낭비가...
            appbarOffsetChangedLive.set { appbar, offset ->
                val maxScroll    = appbar.totalScrollRange
                val percentage = abs(offset).toFloat() / maxScroll.toFloat()

                if (mLog.isTraceEnabled) {
                    mLog.trace("APP BAR (ALPHA) : $percentage")
                }

                mBinding.searchArea.alpha = 1.0f - percentage

                // scroll 되어 offset 된 값을 webview 쪽으로 전달
                appbarOffsetLive.value    = offset
            }
        }

        mRealtimeIssueViewModel.apply {
            observe(commandEvent) { onCommandEvent(it.first, it.second) }

            viewpager.set(mBinding.realtimeIssueViewpager)
            viewPagerLoaded.set { customRealtimeIssueTab() }
        }
    }

    @SuppressLint("InflateParams")
    private fun customRealtimeIssueTab() {
        mBinding.realtimeIssueTab.apply {
            addOnTabSelectedListener(mRealtimeTabSelectedListener)

            tabs.forEach {
                it?.let { tab ->
                    val text = tab.text
                    val custom = LayoutInflater.from(requireContext()).inflate(R.layout.tab_main_custom, null)
                    // kotlin extension 으로 하고 싶긴한데 먼가 editor 문제인지 import 가 안된다.
                    custom.findViewById<TextView>(R.id.tab_label).text = text

                    tab.customView = custom
                }

                mBinding.realtimeIssueTab.tabs[0]?.customView?.let { v ->
                    if (v is TextView) {
                        v.setTypeface(v.typeface, Typeface.BOLD)
                    }
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
        mRealtimeIssueViewModel.disposableInterval.dispose()

        mBinding.apply {
            tab.removeOnTabSelectedListener(this@MainFragment)
            viewpager.adapter = null
            realtimeIssueTab.removeOnTabSelectedListener(mRealtimeTabSelectedListener)
        }

        super.onDestroy()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND EVENT : $cmd")
        }

        MainViewModel.apply {
            // NAVIGATION EDITOR 로 변경해야 되나? -_ -ㅋ
            // 상단 검색쪽 메뉴들은 스크롤 시 클릭 이벤트가 동작하지 않도록 offset 값을 참조 한다.
            if (mViewModel.appbarOffsetLive.value == 0) {
                when (cmd) {
                    CMD_SEARCH_FRAMGNET         -> showSearchFragment()
                    CMD_REALTIME_ISSUE_FRAGMENT -> toggleRealtimeIssueArea()
                    CMD_NAVIGATION_FRAGMENT     -> viewController.navigationFragment()
                    CMD_BRS_OPEN                -> showBrowser(data)
                    CMD_MEDIA_SEARCH_FRAGMENT   -> showMediaSearch()
                }
            }
        }

        RealtimeIssueViewModel.apply {
            when (cmd) {
                CMD_LOADED_ISSUE -> changeRealtimeIssueTab()
                CMD_CLOSE_ISSUE  -> toggleRealtimeIssueArea()
            }
        }
    }

    private fun showSearchFragment() {
        toggleRealtimeIssueArea { viewController.searchFragment() }
    }

    private fun toggleRealtimeIssueArea(visibleCallback: (() -> Unit)? = null) {
        // 개발자가 바뀐건지 기획자가 바뀐건지.. UI 가 통일되지 않고 이건 따로 노는 듯?

        val lp = mBinding.realtimeIssueArea.layoutParams as ConstraintLayout.LayoutParams

        mRealtimeIssueViewModel.apply {
            if (visibleDetail.get() == View.GONE) {
                visibleCallback?.let {
                    it.invoke()
                    return@apply
                }

                visibleDetail.set(View.VISIBLE)

                tabAlpha.set(AnimParams(1f, duration = RealtimeIssueViewModel.ANIM_DURATION))
                bgAlpha.set(tabAlpha.get())
                tabMenuRotation.set(AnimParams(180f, duration = RealtimeIssueViewModel.ANIM_DURATION))
                containerTransY.set(AnimParams(0f, duration = RealtimeIssueViewModel.ANIM_DURATION))

                val currentTabHeight = 0f
                val changeTabHeight = config.SCREEN.y.toFloat() - lp.topMargin

                if (mLog.isDebugEnabled) {
                    mLog.debug("CHANGE TAB HEIGHT : $currentTabHeight -> $changeTabHeight")
                }

                mBinding.realtimeIssueArea.layoutHeight(changeTabHeight)
            } else {
                tabAlpha.set(AnimParams(0f, duration = RealtimeIssueViewModel.ANIM_DURATION
                    , endListener = { v, anim ->
                        if (v.id == mBinding.realtimeIssueTab.id) {
                            visibleDetail.set(View.GONE)
                            mBinding.realtimeIssueArea.layoutHeight(1f)

                            visibleCallback?.invoke()
                        }
                    }))
                tabMenuRotation.set(AnimParams(0f, duration = RealtimeIssueViewModel.ANIM_DURATION))

                val height = mBinding.realtimeIssueViewpager.height * -1f
                containerTransY.set(AnimParams(height, duration = RealtimeIssueViewModel.ANIM_DURATION))

                val currentTabHeight = mBinding.realtimeIssueArea.height.toFloat()
                val changeTabHeight = 0f

                if (mLog.isDebugEnabled) {
                    mLog.debug("CHANGE TAB HEIGHT : $currentTabHeight -> $changeTabHeight")
                }
            }
        }
    }

    private fun changeRealtimeIssueTab() {
        mRealtimeIssueViewModel.apply {
//            tabAdapter.set(RealtimeIssueTabAdapter(childFragmentManager, mRealtimeIssueList!!))
            val adapter = realtimeIssueTabAdapter.get()
            adapter.issueList = mRealtimeIssueList

            tabAdapter.set(adapter)
        }
    }

    private fun showBrowser(url: Any) {
        toggleRealtimeIssueArea { viewController.browserFragment(url.toString()) }
    }

    private fun showMediaSearch() {
        toggleRealtimeIssueArea { viewController.mediaSearchFragment() }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    override fun onBackPressed(): Boolean {
        if (mRealtimeIssueViewModel.visibleDetail.get() == View.VISIBLE) {
            toggleRealtimeIssueArea()
            return true
        }

        return false
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

        mCurrentTabPos = tab.position
        mViewModel.currentTabPositionLive.value = tab.position
    }

    private fun customRealtimeIssueTabText(pos: Int) {
        var i = 0
        mBinding.realtimeIssueTab.tabs.forEach {
            val tv = (it?.customView as TextView)

            if (i++ == pos) {
                tv.setTypeface(tv.typeface, Typeface.BOLD)
            } else {
                tv.setTypeface(tv.typeface, Typeface.NORMAL)
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
        @dagger.android.ContributesAndroidInjector
        abstract fun contributeInjector(): MainFragment

//        @Binds
//        abstract fun bindMainTabAdapter(adapter: MainTabAdapter): FragmentStatePagerAdapter
//
//        @Binds
//        abstract fun bindRealtimeIssueTabAdapter(adapter: RealtimeIssueTabAdapter): FragmentStatePagerAdapter

        @dagger.Module
        companion object {
            @JvmStatic
            @Provides
            @Named("child_fragment_manager")
            fun provideChildFragments(fragment: MainFragment): FragmentManager {
                return fragment.childFragmentManager
            }
        }
    }
}


