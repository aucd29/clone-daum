package com.example.common.bindingadapter

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Interpolator
import androidx.databinding.BindingAdapter

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

// view.animate 가 보기는 좋아서 바꿨다가 기능의 문제로 원래대로 ObjectAnimator 로 변경 OTL
//
object AnimationBindingAdapter {
    private const val K_TRANSLATION_Y = "translationY"
    private const val K_TRANSLATION_X = "translationX"
    private const val K_ALPHA         = "alpha"
    private const val K_SCALE_X       = "scaleX"
    private const val K_SCALE_Y       = "scaleY"

    @JvmStatic
    @BindingAdapter("bindTranslateY")
    fun bindTranslateY(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_Y, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_Y, params.initValue, params.value)
        }

        objectAnim(anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindTranslateX")
    fun bindTranslateX(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_X, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_X, params.initValue, params.value)
        }

        objectAnim(anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindAlpha")
    fun bindAlpha(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_ALPHA, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_ALPHA, params.initValue, params.value)
        }

        objectAnim(anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindScaleX")
    fun bindScaleX(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_SCALE_X, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_SCALE_X, params.initValue, params.value)
        }

        objectAnim(anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindScaleY")
    fun bindScaleY(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_SCALE_Y, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_SCALE_Y, params.initValue, params.value)
        }

        objectAnim(anim, params)
    }

    private fun objectAnim(anim: ObjectAnimator, params: AnimParams) {
        params.run {
            anim.setDuration(duration)
            interpolator?.let { anim.setInterpolator(it) }
            startDelay?.let   { anim.setStartDelay(it) }

            if (reverse != null) {
                anim.repeatMode = ValueAnimator.REVERSE
                anim.repeatCount = repeatCount
            }

            if (endListener != null || reverse != null) {
                anim.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator?) {}
                    override fun onAnimationCancel(animator: Animator?) {}
                    override fun onAnimationRepeat(animator: Animator?) {
                        reverse?.invoke(animator)
                    }
                    override fun onAnimationEnd(animator: Animator?) {
                        endListener?.invoke(animator)
                    }
                })
            }

            if (objAniCallback != null) {
                objAniCallback.invoke(anim)
            } else {
                anim.start()
            }
        }
    }
}

data class AnimParams(
    var value          : Float,
    val initValue      : Float? = null,
    var duration       : Long = 300,
    var endListener    : ((Animator?) -> Unit)? = null,
    var interpolator   : Interpolator? = null,
    var startDelay     : Long? = null,
    var reverse        : ((Animator?) -> Unit)? = null,
    var repeatCount    : Int = 1,
    val objAniCallback : ((ObjectAnimator) -> Unit)? = null
)