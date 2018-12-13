package com.example.clone_daum.ui.main

import android.os.Bundle
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.ui.search.SearchFragment
import com.example.common.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.tab_main_custom.view.*
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainFragment : BaseRuleFragment<MainFragmentBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)
    }

    @Inject lateinit var disposable: CompositeDisposable
    @Inject lateinit var vmfactory: DaggerViewModelFactory

    lateinit var viewmodel: MainViewModel

    // 먼가 룰에 따라서 viewmodel 을 inject 시키긴 하는데 맘에 안드는 군;;;
    override fun bindViewModel() {
        viewmodel = vmfactory.inject(this, MainViewModel::class.java)
        mBinding.model = viewmodel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (mLog.isDebugEnabled) {
            mLog.debug("START MAIN FRAGMENT")
        }

        initFragment()
    }

    private fun initFragment() {
        mBinding.run {
            viewpager.offscreenPageLimit = 3
        }

        viewmodel.run {
            settingTab(tabDataList)
            settingEvents()
        }
    }

    private fun settingTab(tabList: List<TabData>) {
        if (mLog.isDebugEnabled) {
            mLog.debug("TAB SIZE : ${tabList.size}")
            mLog.debug("TAB DATA : ${tabList}")
        }

        viewmodel.run {
            // fixme main tab adapter 이건 고민 해봐야 될 듯 -_-;
            tabAdapter.set(MainTabAdapter(childFragmentManager, tabList))
            viewpager.set(mBinding.viewpager)
            viewpagerLoadedEvent.set {
                if (mLog.isDebugEnabled) {
                    mLog.debug("TAB LOADED")
                    mLog.debug("tab count = ${mBinding.tab.tabCount}")
                }

                // https://stackoverflow.com/questions/40896907/can-a-custom-view-be-used-as-a-tabitem
                mBinding.tab.childList.forEach {
                    val view = layoutInflater.inflate(R.layout.tab_main_custom, null)
                    view.tab_label.text = it?.text

                    it?.customView = view
                }
            }
        }
    }

    private fun settingEvents() = viewmodel.run {
        observe(gotoSearchEvent) {
            activity().supportFragmentManager.show(FragmentParams(R.id.container,
                SearchFragment::class.java, anim = FragmentAnim.ALPHA))
        }

        appbarOffsetChangedEvent.set { appbar, offset ->
            val maxScroll = appbar.getTotalScrollRange()
            val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

            if (mLog.isTraceEnabled) {
                mLog.trace("$percentage")
            }

            mBinding.searchArea.alpha = 1.0f - percentage
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
    }
}
