package com.example.common.bindingadapter

import android.graphics.Interpolator
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.databinding.BindingAdapter

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

object AnimationBindingAdapter {
    @JvmStatic
    @BindingAdapter("bindTranslateY")
    fun bindTranslateY(view: View, params: AnimParams) {
        params.initValue?.run { view.translationY = this }
        animStart(view.animate().translationY(params.value), params)
    }

    @JvmStatic
    @BindingAdapter("bindTranslateX")
    fun bindTranslateX(view: View, params: AnimParams) {
        params.initValue?.run { view.translationX = this }
        animStart(view.animate().translationX(params.value), params)
    }

    @JvmStatic
    @BindingAdapter("bindAlpha")
    fun bindAlpha(view: View, params: AnimParams) {
        params.initValue?.run { view.alpha = this }
        animStart(view.animate().alpha(params.value), params)
    }

    private fun animStart(anim: ViewPropertyAnimator, params: AnimParams) {
        params.interpolator?.let { anim.setInterpolator { it } }
        params.endListener?.let { anim.withEndAction(it) }

        anim.setDuration(params.duration).start()
    }
}

data class AnimParams(
    val value: Float,
    val initValue: Float? = null,
    val duration: Long = 300,
    val endListener: (() -> Unit)? = null,
    val interpolator: Interpolator? = null
)