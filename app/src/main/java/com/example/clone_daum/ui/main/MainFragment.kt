package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueTabAdapter
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import com.example.clone_daum.ui.search.PopularViewModel
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.di.dagger.scope.FragmentScope
import brigitte.widget.observeTabPosition
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import com.example.clone_daum.common.Config
import com.example.clone_daum.databinding.TabMainCustomBinding
import com.example.clone_daum.di.module.AssistedViewModelKey
import com.example.clone_daum.di.module.DaggerSavedStateViewModelFactory
import com.example.clone_daum.di.module.ViewModelAssistedFactory
import com.example.clone_daum.ui.Navigator
import dagger.Binds
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

class MainFragment constructor(
) : BaseDaggerFragment<MainFragmentBinding, MainViewModel>(), OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)

        fun create() = MainFragment()
    }

    init {
        mViewModelScope = SCOPE_ACTIVITY    // MainViewModel 를 MainWebViewFragment 와 공유
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var config: Config
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var mainTabAdapter: MainTabAdapter
    @Inject lateinit var realtimeIssueTabAdapter: Provider<RealtimeIssueTabAdapter>
    @Inject lateinit var factory: DaggerSavedStateViewModelFactory

    override val layoutId = R.layout.main_fragment

    private val mIssueViewModel: RealtimeIssueViewModel by activityInject()
    private val mPopularViewModel: PopularViewModel by activityInject()
    private val mAssignedInjectViewModel: AssignedInjectTestViewModel by stateInject { factory }

    override fun bindViewModel() {
        super.bindViewModel()

        mBinding.issueModel = mIssueViewModel
        addCommandEventModels(mIssueViewModel)
    }

    override fun initViewBinding() {
        mBinding.apply {
            // MAIN TAB
            mainWebViewpager.adapter = mainTabAdapter
            mainTab.setupWithViewPager(mainWebViewpager)

            // ISSUE TAB
            mainIssueTab.setupWithViewPager(mainIssueViewpager)

            mainAppbar.globalLayoutListener {
                val result = realtimeIssueAreaMargin()

                mViewModel.appbarHeight(mainAppbar.height, mainTabContainer.height)
                mIssueViewModel.layoutTranslationY.set(mainIssueViewpager.height * -1f)

                result
            }
        }
    }

    private fun realtimeIssueAreaMargin(): Boolean {
        mBinding.apply {
            mainIssueContainer.apply {
                // 검색쪽 위치까지 margin 이동
                (layoutParams as ConstraintLayout.LayoutParams).let {
                    it.topMargin = mainToolbarContainer.height - mainToolbarUnderline.height

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
        observe(mAssignedInjectViewModel.testLive) {
            if (mLog.isDebugEnabled) {
                mLog.debug("LIVE DATA HELLO ${it.toUpperCase()}")
            }
        }

        mIssueViewModel.apply {
            loadData()
            observe(htmlDataLive) { mPopularViewModel.load(it) }
            observeTabPosition(tabChangedLive, ::realtimeIssueTabFocusText)
        }

        mViewModel.apply {
            mainContainerDispatchTouchEvent()
            appbarAlpha()

            observe(appbarMagneticEffectLive) {
                mBinding.mainAppbar.setExpanded(it, true)
            }
        }
    }

    override fun onDestroy() {
        mBinding.apply {
            mainWebViewpager.adapter   = null
            mainIssueViewpager.adapter = null
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
                    CMD_SEARCH_FRAGMENT       -> navigateSearchFragment()
                    CMD_NAVIGATION_FRAGMENT   -> navigateNavigationFragment()
                    CMD_MEDIA_SEARCH_FRAGMENT -> navigateMediaSearchFragment()
                    CMD_REALTIME_ISSUE        -> toggleIssueLayout()
                    CMD_BRS_OPEN              -> navigateBrowserFragment(data)
                }
            }
        }

        RealtimeIssueViewModel.apply {
            when (cmd) {
                CMD_LOADED_ISSUE -> loadRealtimeIssueTab()
                CMD_CLOSE_ISSUE  -> toggleIssueLayout()
            }
        }
    }

    private fun navigateSearchFragment() {
        toggleIssueLayout { navigator.searchFragment() }
    }

    private fun navigateNavigationFragment() {
        navigator.navigationFragment()
    }

    private fun toggleIssueLayout(visibleCallback: (() -> Unit)? = null) {
        // 개발자가 바뀐건지 기획자가 바뀐건지.. UI 가 통일되지 않고 이건 따로 노는 듯?
        val lp = mBinding.mainIssueContainer.layoutParams as ConstraintLayout.LayoutParams

        mIssueViewModel.apply {
            if (viewRealtimeIssue.isGone()) {
                visibleCallback?.let {
                    it.invoke()
                    return@apply
                }

                viewRealtimeIssue.visible()

                tabAlpha.set(AnimParams(1f, duration = RealtimeIssueViewModel.ANIM_DURATION))
                backgroundAlpha.set(AnimParams(1f, duration = RealtimeIssueViewModel.ANIM_DURATION))
                bgAlpha.set(tabAlpha.get())

                tabMenuRotation.set(AnimParams(180f, duration = RealtimeIssueViewModel.ANIM_DURATION))
                containerTransY.set(AnimParams(0f, duration = RealtimeIssueViewModel.ANIM_DURATION))

                val currentTabHeight = 0f
                val changeTabHeight = config.SCREEN.y.toFloat() - lp.topMargin

                if (mLog.isDebugEnabled) {
                    mLog.debug("CHANGE TAB HEIGHT : $currentTabHeight -> $changeTabHeight")
                }

                mBinding.mainIssueContainer.layoutHeight(changeTabHeight)
            } else {
                tabAlpha.set(AnimParams(0f, duration = RealtimeIssueViewModel.ANIM_DURATION
                    , endListener = { _ ->
                        viewRealtimeIssue.gone()
                        mBinding.mainIssueContainer.layoutHeight(1f)

                        visibleCallback?.invoke()
                    }))
                backgroundAlpha.set(AnimParams(0f, duration = RealtimeIssueViewModel.ANIM_DURATION))
                tabMenuRotation.set(AnimParams(0f, duration = RealtimeIssueViewModel.ANIM_DURATION))

                val height = mBinding.mainIssueViewpager.height * -1f
                containerTransY.set(AnimParams(height, duration = RealtimeIssueViewModel.ANIM_DURATION))

                val currentTabHeight = mBinding.mainIssueContainer.height.toFloat()
                val changeTabHeight = 0f

                if (mLog.isDebugEnabled) {
                    mLog.debug("CHANGE TAB HEIGHT : $currentTabHeight -> $changeTabHeight")
                }
            }
        }
    }

    private fun loadRealtimeIssueTab() {
        mIssueViewModel.apply {
            val adapter = realtimeIssueTabAdapter.get()
            adapter.issueList = mRealtimeIssueList

            mBinding.mainIssueViewpager.adapter = adapter

            customRealtimeIssueTab()
        }
    }

    @SuppressLint("InflateParams")
    private fun customRealtimeIssueTab() {
        mBinding.mainIssueTab.apply {
            tabs.forEach {
                it?.let { tab ->
                    val binding = dataBinding<TabMainCustomBinding>(R.layout.tab_main_custom)
                    binding.tabLabel.text = tab.text
                    tab.customView        = binding.tabLabel
                }

                mBinding.mainIssueTab.tabs[0]?.customView?.let { v -> if (v is TextView) { v.bold() } }
            }
        }
    }

    private fun navigateMediaSearchFragment() {
        toggleIssueLayout {
            navigator.mediaSearchFragment()
        }
    }

    private fun navigateBrowserFragment(url: Any) {
        toggleIssueLayout {
            navigator.browserFragment(url.toString())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed(): Boolean {
        if (mIssueViewModel.viewRealtimeIssue.isVisible()) {
            toggleIssueLayout()

            return true
        }

        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // REALTIME ISSUE TAB CHANGED
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun realtimeIssueTabFocusText(pos: Int) {
        var i = 0
        mBinding.mainIssueTab.tabs.forEach {
            val tv = it?.customView
            if (tv is TextView) {
                if (i++ == pos) {
                    tv.bold()
                } else {
                    tv.normal()
                }
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
        @FragmentScope
        @dagger.android.ContributesAndroidInjector(modules = [MainFragmentModule::class])
        abstract fun contributeMainFragmentInjector(): MainFragment
    }

    @dagger.Module
    abstract class MainFragmentModule {
        @Binds
        abstract fun bindMainFragment(fragment: MainFragment): Fragment

        @Binds
        @IntoMap
        @AssistedViewModelKey(AssignedInjectTestViewModel::class)
        abstract fun bindAssignedInjectTestViewModelFactory(vm: AssignedInjectTestViewModel.Factory)
                : ViewModelAssistedFactory<out ViewModel>

        @Binds
        abstract fun bindSavedStateRegistryOwner(fragment: MainFragment): SavedStateRegistryOwner

        @dagger.Module
        companion object {
            @JvmStatic
            @Provides
            fun provideMainFragmentManager(fragment: Fragment) =
                fragment.childFragmentManager
        }
    }
}
