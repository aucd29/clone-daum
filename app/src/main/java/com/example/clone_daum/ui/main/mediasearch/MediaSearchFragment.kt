package com.example.clone_daum.ui.main.mediasearch

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import com.example.clone_daum.databinding.MediaSearchFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 16. <p/>
 *
 */

// 다이얼로그로 하는게 나으려나?
class MediaSearchFragment : BaseDaggerFragment<MediaSearchFragmentBinding, MediaSearchViewModel>()
    , OnBackPressedListener {

    companion object {
        private const val ANIM_DURATION = 400L
    }
//
//    @Inject lateinit var config: Config
//
//    override fun onStart() {
//        super.onStart()
//
//        dialog.run {
//            window?.run {
//                setGravity(Gravity.TOP or Gravity.START)
//                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//
//                val lp = attributes
//                lp.x = WindowManager.LayoutParams.MATCH_PARENT
//                lp.y = WindowManager.LayoutParams.WRAP_CONTENT
//                attributes = lp
//
//                setBackgroundDrawableResource(android.R.color.transparent)
//
//                addFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
//            }
//        }
//    }

    override fun initViewBinding() = mBinding.run {
        mediaSearchExtendMenuContainer.run {
            layoutListener {
                translationY = height.toFloat() * -1

                animateIn()
            }
        }
    }

    override fun initViewModelEvents() = mViewModel.run {

    }

    override fun onBackPressed(): Boolean {
        animateOut()
        return true
    }

    private fun animateIn() {
        mViewModel.run {
            dimmingBgAlpha.set(AnimParams(1f, duration = ANIM_DURATION))
            containerTransY.set(AnimParams(0f, duration = ANIM_DURATION))
        }
    }

    private fun animateOut() {
        mViewModel.run {
            dimmingBgAlpha.set(AnimParams(0f, duration = ANIM_DURATION))
            containerTransY.set(AnimParams(
                mBinding.mediaSearchExtendMenuContainer.height.toFloat() * -1
                , duration    = ANIM_DURATION
                , endListener = { finish() }
                , interpolator = AccelerateInterpolator()))
        }
    }

    override fun onCommandEvent(cmd: String, data: Any?) = MediaSearchViewModel.run {
        when (cmd) {
            CMD_ANIM_FINISH -> onBackPressed()
        }

        Unit
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MediaSearchFragment
    }
}