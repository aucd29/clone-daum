package com.example.clone_daum.ui.main.realtimeissue

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.clone_daum.R
import com.example.clone_daum.databinding.RealtimeIssueFragmentBinding
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerBottomSheetDialogFragment
import com.example.common.finish
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 9. <p/>
 *
 * rounded dialog
 * - https://gist.github.com/ArthurNagy/1c4a64e6c8a7ddfca58638a9453e4aed
 */

class RealtimeIssueFragment
    : BaseDaggerBottomSheetDialogFragment<RealtimeIssueFragmentBinding, RealtimeIssueViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueFragment::class.java)
    }

    init {
        // RealtimeIssueViewModel 를 MainFragment 와 공유
        mViewModelScope = SCOPE_ACTIVITY
    }

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var viewController: ViewController

    // 라운드 다이얼로그로 수정
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(context!!, R.style.round_bottom_sheet_dialog)

    override fun initViewBinding() { }

    override fun initViewModelEvents() {
        mViewModel.run {
            mRealtimeIssueList?.let {
                if (mLog.isDebugEnabled) {
                    mLog.debug("INIT TAB ADAPTER")
                }

                tabAdapter.set(RealtimeIssueTabAdapter(childFragmentManager, it))
                viewpager.set(mBinding.realtimeIssueViewpager)
            }
        }
    }

    override fun onCommandEvent(cmd: String, data: Any?) {
        when(cmd) {
            RealtimeIssueViewModel.CMD_BRS_OPEN -> {
                viewController.browserFragment(data.toString())
                dismiss()
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
        @ContributesAndroidInjector
        abstract fun contributeInjector(): RealtimeIssueFragment
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// RealtimeIssueTabAdapter
//
////////////////////////////////////////////////////////////////////////////////////

class RealtimeIssueTabAdapter constructor(fm: FragmentManager,
    val mRealtimeIssue: List<Pair<String, List<RealtimeIssue>>>)
    : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val frgmt  = RealtimeIssueChildFragment()
        val bundle = Bundle()
        bundle.putInt("position", position)

        frgmt.arguments = bundle

        return frgmt
    }

    // 이슈 검색어 단어는 삭제하고 타이틀을 생성한다.
    override fun getPageTitle(position: Int) =
        mRealtimeIssue.get(position).first

    override fun getCount() = mRealtimeIssue.size
}