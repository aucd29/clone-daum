package com.example.clone_daum.ui.main.mediasearch

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.view.animation.*
import android.widget.ViewAnimator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProviders
import com.example.clone_daum.databinding.MediaSearchFragmentBinding
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 16. <p/>
 *
 */

// 다이얼로그로 하는게 나으려나?
class MediaSearchFragment : BaseDaggerFragment<MediaSearchFragmentBinding, MediaSearchViewModel>()
    , OnBackPressedListener {

    companion object {
        private val mLog = LoggerFactory.getLogger(MediaSearchFragment::class.java)

        private const val ANIM_DURATION     = 400L
        private const val ANIM_START_DELAY  = 250L
    }

    override fun viewModelProvider()
        = ViewModelProviders.of(this, mViewModelFactory).get(viewModelClass())

    override fun initViewBinding() = mBinding.run {
        mediaSearchExtendMenuContainer.run {
            layoutListener {
                translationY = height.toFloat() * -1
                mBinding.mediaSearchButtonLayout.translationY =
                        mBinding.mediaSearchButtonLayout.height.toFloat() * -1

                animateIn()
            }
        }

        mediaSearchBackground.run {
            layoutListener {
                // 숨김 영역을 조금 감춤

                val newHeight = height + 20.dpToPx(context!!)
                if (mLog.isDebugEnabled) {
                    mLog.debug("MEDIA SEARCH HEIGHT : ${height} -> ${newHeight}")
                }

                layoutHeight(newHeight)
            }
        }
    }

    override fun initViewModelEvents() = mViewModel.run {

    }

    override fun onBackPressed(): Boolean {
        animateOut()
        return true
    }

    lateinit var objani: ObjectAnimator

    private fun animateIn() {
        mViewModel.run {
            dimmingBgAlpha.set(AnimParams(1f, duration = ANIM_DURATION))
            containerTransY.set(AnimParams(0f, duration = ANIM_DURATION))
//            bounceTransY.set(AnimParams(0f
//                , initValue    = mBinding.mediaSearchButtonLayout.height.toFloat() * -1
//                , duration     = ANIM_DURATION
//                , startDelay   = ANIM_START_DELAY
//                , interpolator = OvershootInterpolator(2.3f)
//            ))
            objani = ObjectAnimator.ofFloat(mBinding.mediaSearchButtonLayout
                , "translationY"
                , mBinding.mediaSearchButtonLayout.height.toFloat() * -1
                , 0f)
            objani.setDuration(ANIM_DURATION)
            objani.setInterpolator(OvershootInterpolator(2.3f))
            objani.setStartDelay(ANIM_START_DELAY)
            objani.repeatCount = 1
            objani.repeatMode  = ValueAnimator.REVERSE
            objani.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {}
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationRepeat(p0: Animator?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        p0?.pause()
                    }
                }
                override fun onAnimationEnd(animation: Animator?) {}
            })

            objani.start()
        }
    }

    private fun animateOut() {
        mViewModel.run {
            dimmingBgAlpha.set(AnimParams(0f
                , duration     = ANIM_DURATION
                , startDelay   = ANIM_START_DELAY
            ))
            containerTransY.set(AnimParams(
                mBinding.mediaSearchExtendMenuContainer.height.toFloat() * -1
                , duration     = ANIM_DURATION
                , startDelay   = ANIM_START_DELAY
                , endListener  = { finish() }
            ))

            objani.resume()

//            bounceTransY.set(AnimParams(mBinding.mediaSearchButtonLayout.height.toFloat() * -1
//                , duration     = ANIM_DURATION
//                , interpolator = OvershootInterpolator(2.3f)
//                , reverse      = true
//            ))
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