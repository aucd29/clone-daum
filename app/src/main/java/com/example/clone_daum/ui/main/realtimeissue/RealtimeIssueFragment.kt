package com.example.clone_daum.ui.main.realtimeissue

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.clone_daum.R
import com.example.clone_daum.databinding.RealtimeIssueFragmentBinding
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.ViewController
import brigitte.*
import brigitte.bindingadapter.AnimParams
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 9. <p/>
 *
 * rounded dialog
 * - https://gist.github.com/ArthurNagy/1c4a64e6c8a7ddfca58638a9453e4aed
 *
 * 디자인이 변경됨 =_ = [aucd29][2019. 2. 22.]
 */

////////////////////////////////////////////////////////////////////////////////////
//
// RealtimeIssueTabAdapter
//
////////////////////////////////////////////////////////////////////////////////////

class RealtimeIssueTabAdapter constructor(fm: FragmentManager
    , val mRealtimeIssue: List<Pair<String, List<RealtimeIssue>>>
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val frgmt  = RealtimeIssueChildFragment()
        val bundle = Bundle()
        bundle.putInt("position", position)

        frgmt.arguments = bundle

        return frgmt
    }

    // 이슈 검색어 단어는 삭제하고 타이틀을 생성한다.
    override fun getPageTitle(position: Int) =
        mRealtimeIssue[position].first

    override fun getCount() = mRealtimeIssue.size
}

//class RealtimeIssueFragment
//    : BaseDaggerFragment<RealtimeIssueFragmentBinding, RealtimeIssueViewModel>()
//    , OnBackPressedListener {
//    companion object {
//        private val mLog = LoggerFactory.getLogger(RealtimeIssueFragment::class.java)
//
//        private const val ANIM_DURATION     = 200L
//    }
//
//    init {
//        // RealtimeIssueViewModel 를 MainFragment 와 공유
//        mViewModelScope = BaseFragment.SCOPE_ACTIVITY
//    }
//
//    @Inject lateinit var preConfig: PreloadConfig
//    @Inject lateinit var viewController: ViewController
//
////    // 라운드 다이얼로그로 수정
////    override fun onCreateDialog(savedInstanceState: Bundle?) =
////        BottomSheetDialog(requireContext(), R.style.round_bottom_sheet_dialog)
//
//    override fun initViewBinding() {
//        mBinding.apply {
//            realtimeIssueViewpager.apply {
//                globalLayoutListener {
//                    translationY = height.toFloat() * -1
//
//                    startAnimation()
//                }
//            }
//
//            realtimeIssueBackground.apply {
//                globalLayoutListener {
//                    val newHeight = height + 20.dpToPx(requireContext())
//                    if (mLog.isDebugEnabled) {
//                        mLog.debug("REALTIME ISSUE HEIGHT : ${height} -> ${newHeight}")
//                    }
//
//                    layoutHeight(newHeight)
//                }
//            }
//        }
//    }
//
//    override fun initViewModelEvents() {
//        mViewModel.apply {
//            mRealtimeIssueList?.let {
//                if (mLog.isDebugEnabled) {
//                    mLog.debug("INIT TAB ADAPTER")
//                }
//
//                tabAdapter.set(RealtimeIssueTabAdapter(childFragmentManager, it))
//                viewpager.set(mBinding.realtimeIssueViewpager)
//            }
//        }
//    }
//
//    override fun onBackPressed(): Boolean {
//        if (mLog.isDebugEnabled) {
//            mLog.debug("BACK PRESSED")
//        }
//
//        endAnimation()
//        return true
//    }
//
//    private fun startAnimation() {
////        val height = mBinding.realtimeIssueLayout.height.toFloat() * -1
////        val overshootAnim = AnimParams(0f
////            , initValue    = height
////            , duration     = ANIM_DURATION
////            , startDelay   = ANIM_START_DELAY
////        )
////
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            overshootAnim.reverse = {
////                mPauseAnimator = it
////                it?.pause()
////            }
////        }
//
//        mViewModel.apply {
//            // animate set 을 쓰는게 나으려나?
//            dimmingBgAlpha.set(AnimParams(1f, duration = ANIM_DURATION))
//            containerTransY.set(AnimParams(0f, duration = ANIM_DURATION))
//            tabMenuRotation.set(AnimParams(180f, duration = ANIM_DURATION))
////            overshootTransY.set(overshootAnim)
//        }
//    }
//
//    private fun endAnimation(endCallback: (() -> Unit)? = null) {
//        val height = mBinding.realtimeIssueViewpager.height.toFloat() * -1
//        val dimmingBgAlphaAnim  = AnimParams(0f, duration = ANIM_DURATION)
//        val containerTransYAnim = AnimParams(height, duration = ANIM_DURATION
//            , endListener = {
//                finish()
//                endCallback?.invoke()
//            })
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            dimmingBgAlphaAnim.startDelay  = ANIM_START_DELAY
////            containerTransYAnim.startDelay = ANIM_START_DELAY
////            mPauseAnimator?.resume()
////        }
//
//        mViewModel.apply {
//            dimmingBgAlpha.set(dimmingBgAlphaAnim)
//            containerTransY.set(containerTransYAnim)
//            tabMenuRotation.set(AnimParams(0f, duration = ANIM_DURATION))
//        }
//    }
//
//    override fun onCommandEvent(cmd: String, data: Any) {
//        RealtimeIssueViewModel.apply {
//            when(cmd) {
//                CMD_ANIM_FINISH -> onBackPressed()
//                CMD_BRS_OPEN    -> endAnimation {
//                    viewController.browserFragment(data.toString())
//                }
//            }
//        }
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////
//    //
//    // Module
//    //
//    ////////////////////////////////////////////////////////////////////////////////////
//
//    @dagger.Module
//    abstract class Module {
//        @ContributesAndroidInjector
//        abstract fun contributeInjector(): RealtimeIssueFragment
//    }
//}
