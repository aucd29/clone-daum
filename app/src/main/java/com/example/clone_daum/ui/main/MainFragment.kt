package com.example.clone_daum.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.model.DataManager
import com.example.clone_daum.model.local.TabData
import com.example.common.BaseRuleFragment
import com.example.common.jsonParse
import com.example.common.viewModel
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

        loadDataManager()
    }

    private fun loadDataManager() {
        context?.let {
            DataManager.tabList(it).subscribe {
                it.jsonParse<List<TabData>>().let {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("TAB SIZE : ${it.size}")
                        mLog.debug("TAB DATA : ${it}")
                    }

                    viewmodel()?.let { vm ->
                        vm.viewpager.set(mBinding.viewpager)
                        vm.tabAdapter.set(TabAdapter(childFragmentManager, it))

                        observe(vm.webviewInit) {

                        }
                    }
                }
            }
        }

    }

    private fun loadArgs() {
        arguments?.let {
            it.getByteArray("tabList")?.jsonParse<List<TabData>>()?.let {
                if (mLog.isDebugEnabled) {
                    mLog.debug("TAB SIZE : ${it.size}")
                    mLog.debug("TAB DATA : ${it}")
                }

                viewmodel()?.let { vm ->
                    vm.viewpager.set(mBinding.viewpager)
                    vm.tabAdapter.set(TabAdapter(childFragmentManager, it))

                    observe(vm.webviewInit) {
                        activity?.let {
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding.viewpager.adapter = null
    }
}

class TabAdapter(fm: FragmentManager?, val tabData: List<TabData>) : FragmentPagerAdapter(fm) {
    companion object {
        private val mLog = LoggerFactory.getLogger(TabAdapter::class.java)
    }

    override fun getItem(position: Int): Fragment {
        val frgmt = MainWebviewFragment()

        val bundle = Bundle()
        bundle.putString("url", tabData[position].url)
        frgmt.arguments = bundle

        return frgmt
    }

    override fun getPageTitle(position: Int): String {
        if (mLog.isDebugEnabled) {
            mLog.debug("TITLE : ${tabData[position].name}")
        }

        return tabData[position].name
    }

    override fun getCount() = tabData.size

//        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
//            val frgmt = obj as Fragment
//            val manager = frgmt.fragmentManager
//
//            manager?.run {
//
//            } ?: super.destroyItem(container, position, obj)
//        }
}
