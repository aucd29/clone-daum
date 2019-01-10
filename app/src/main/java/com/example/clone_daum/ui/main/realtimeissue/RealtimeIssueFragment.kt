package com.example.clone_daum.ui.main.realtimeissue

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.clone_daum.R
import com.example.clone_daum.databinding.RealtimeIssueFragmentBinding
import com.example.clone_daum.di.module.PreloadConfig
import com.example.common.BaseDaggerBottomSheetDialogFragment
import com.example.common.tabs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.ContributesAndroidInjector
import kotlinx.android.synthetic.main.tab_main_custom.view.*
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 9. <p/>
 *
 * rounded dialog
 * - https://gist.github.com/ArthurNagy/1c4a64e6c8a7ddfca58638a9453e4aed
 *
 */

class RealtimeIssueFragment
    : BaseDaggerBottomSheetDialogFragment<RealtimeIssueFragmentBinding, RealtimeIssueViewModel>() {

    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueFragment::class.java)
    }

    @Inject lateinit var preConfig: PreloadConfig

    private val mLabelList: ArrayList<String> = arrayListOf()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(context!!, R.style.round_bottom_sheet_dialog)

    override fun initViewBinding() = mBinding.run {
        preConfig.realtimeIssueMap.forEach({
            if (mLog.isDebugEnabled) {
                mLog.debug("TAB LABEL : ${it.key}")
            }

            mLabelList.add(it.key)
        })

//        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        realtimeIssueTab.tabs.forEach {
//            val view     = inflate(R.layout.realtime_tab_custom)
//            view.tab_label.text = it?.text
//
//            it?.customView = view
//        }

    }

    override fun initViewModelEvents() = mViewModel.run {
        tabAdapter.set(RealtimeIssueTabAdapter(childFragmentManager, mLabelList))
        viewpager.set(mBinding.realtimeIssueViewpager)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): RealtimeIssueFragment
    }
}

class RealtimeIssueTabAdapter constructor(fm: FragmentManager, val mLabelList: ArrayList<String>)
    : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val frgmt  = RealtimeIssueChildFragment()
        val bundle = Bundle()
        bundle.putString("key", mLabelList.get(position))

        frgmt.arguments = bundle

        return frgmt
    }

    // 이슈 검색어 단어는 삭제하고 타이틀을 생성한다.
    override fun getPageTitle(position: Int) =
        mLabelList.get(position).replace(" 이슈검색어", "")

    override fun getCount() = mLabelList.size
}