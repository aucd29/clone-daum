package com.example.clone_daum.ui.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.clone_daum.MainApp
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.model.Repository
import com.example.clone_daum.model.local.TabData
import com.example.clone_daum.ui.search.SearchFragment
import com.example.common.*
import com.example.common.childList
import com.example.common.jsonParse
import com.example.common.viewModel
import kotlinx.android.synthetic.main.tab_main_custom.view.*
import org.slf4j.LoggerFactory

class MainFragment : BaseRuleFragment<MainFragmentBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)
    }

    override fun bindViewModel() {
        mBinding.model = viewmodel()
    }

    private fun viewmodel() = viewModel(MainViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initFragment()
    }

    private fun initFragment() {
        activity().disposable.add(Repository.tabList(context!!).subscribe {
            it.jsonParse<List<TabData>>().let(::settingTab)
            settingEvents()
        })
    }

    private fun settingTab(tabList: List<TabData>) {
        if (mLog.isDebugEnabled) {
            mLog.debug("TAB SIZE : ${tabList.size}")
            mLog.debug("TAB DATA : ${tabList}")
        }

        viewmodel()?.run {
            tabAdapter.set(TabAdapter(childFragmentManager, tabList))
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

//                    val lparams = it?.view.layoutParams
//                    it?.view.layoutParams =

                    it?.customView = view
                }
            }
        }
    }

    private fun settingEvents() = viewmodel()?.run {
        observe(gotoSearchEvent) {
            activity().supportFragmentManager.replace(
                FragmentParams(
                    R.id.container, SearchFragment::class.java,
                    anim = FragmentAnim.ALPHA
                )
            )
        }

        appbarOffsetChangedEvent.set { appbar, offset ->
            val maxScroll = appbar.getTotalScrollRange()
            val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

            if (mLog.isDebugEnabled) {
                mLog.debug("$percentage")
            }

            mBinding.searchArea.alpha = 1.0f - percentage
        }
    }

//    private fun appbarSettings() {
//        mBinding.searchBar.addOnOffsetChangedListener(object: AppBarLayout.OnOffsetChangedListener {
//            override fun onOffsetChanged(appbar: AppBarLayout, offset: Int) {
//                val maxScroll = appbar.getTotalScrollRange()
//                val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()
//
//                if (mLog.isDebugEnabled) {
//                    mLog.debug("$percentage")
//                }
//
//                mBinding.searchArea.alpha = 1.0f - percentage
//
//            }
//        })
//    }

    override fun onDestroy() {
        mBinding.viewpager.adapter = null

        super.onDestroy()
    }
}

class TabAdapter(fm: FragmentManager?, val tabData: List<TabData>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val frgmt = MainWebviewFragment()
        val bundle = Bundle()
        bundle.putString("url", tabData[position].url)

        frgmt.arguments = bundle

        return frgmt
    }

    override fun getPageTitle(position: Int): String = tabData[position].name
    override fun getCount() = tabData.size
}
