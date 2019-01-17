package com.example.common.bindingadapter

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.Interpolator
import androidx.databinding.BindingAdapter
import com.example.common.aniEndListener

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

object AnimationBindingAdapter {
    @JvmStatic
    @BindingAdapter("bindTranslateY")
    fun bindTranslateY(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, "translationY", params.value)
        } else {
            ObjectAnimator.ofFloat(view, "translationY", params.initValue, params.value)
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
            ObjectAnimator.ofFloat(view, "translationX", params.value)
        } else {
            ObjectAnimator.ofFloat(view, "translationX", params.initValue, params.value)
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
            ObjectAnimator.ofFloat(view, "alpha", params.value)
        } else {
            ObjectAnimator.ofFloat(view, "alpha", params.initValue, params.value)
        }

        objectAnim(anim, params)
    }

    private fun objectAnim(anim: ObjectAnimator, params: AnimParams) {
        params.run {
            anim.setDuration(duration)
            endListener?.let { anim.aniEndListener(it) }
            interpolator?.let { anim.setInterpolator(it) }
            startDelay?.let { anim.setStartDelay(it) }
            if (reverse) {
                anim.repeatMode = ValueAnimator.REVERSE
                anim.repeatCount = 1
                anim.reverse()
            }

            anim.start()
        }
    }
}

data class AnimParams(
    val value: Float,
    val initValue: Float? = null,
    val duration: Long = 300,
    val endListener: ((Animator?) -> Unit)? = null,
    val interpolator: Interpolator? = null,
    val startDelay: Long? = null,
    val reverse: Boolean = false
)