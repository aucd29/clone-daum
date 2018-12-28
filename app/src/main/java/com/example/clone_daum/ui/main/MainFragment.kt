package com.example.clone_daum.ui.main

import com.example.clone_daum.R
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import kotlinx.android.synthetic.main.tab_main_custom.view.*
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainFragment : BaseDaggerFragment<MainFragmentBinding, MainViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController
//    @Inject lateinit var mainTabAdapter: MainTabAdapter2

    override fun settingEvents() = mViewModel.run {
        observe(gotoSearchEvent) { viewController.searchFragment() }
        observe(navEvent)        { viewController.navigationFragment() }

        // fixme main tab adapter 이건 고민 해봐야 될 듯 -_-;
        tabAdapter.set(MainTabAdapter(childFragmentManager, preConfig.tabLabelList))
        viewpager.set(mBinding.viewpager)
        viewpagerLoadedEvent.set {
            if (mLog.isDebugEnabled) {
                mLog.debug("TAB LOADED (COUNT :${mBinding.tab.tabCount})")
            }

            // https://stackoverflow.com/questions/40896907/can-a-custom-view-be-used-as-a-tabitem
            mBinding.tab.tabs.forEach {
                val view = layoutInflater.inflate(R.layout.tab_main_custom, null)
                view.tab_label.text = it?.text

                it?.customView = view
            }
        }

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

    override fun onDestroy() {
        mBinding.viewpager.adapter = null

        super.onDestroy()
    }
    
    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @dagger.android.ContributesAndroidInjector
        abstract fun contributeInjector(): MainFragment

//        @Binds
//        @FragmentKey(MainFragment::class)
//        abstract fun bindMainTabAdapter(adapter: MainTabAdapter2): FragmentStatePagerAdapter
    }
}


