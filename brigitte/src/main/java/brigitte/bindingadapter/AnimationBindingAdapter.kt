@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.bindingadapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Interpolator
import androidx.databinding.BindingAdapter
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

// view.animate 가 보기는 좋아서 바꿨다가 기능의 문제로 원래대로 ObjectAnimator 로 변경 OTL
//
object AnimationBindingAdapter {
    private val mLog = LoggerFactory.getLogger(AnimationBindingAdapter::class.java)

    const val K_TRANSLATION_X = "translationX"
    const val K_TRANSLATION_Y = "translationY"
    const val K_ALPHA         = "alpha"
    const val K_SCALE_X       = "scaleX"
    const val K_SCALE_Y       = "scaleY"
    const val K_ROTATION      = "rotation"

    @JvmStatic
    @BindingAdapter("bindAnimatorSet")
    fun bindObjectAnim(view: View, params: AnimatorSetParams?) {
        if (params == null) {
            return
        }

        val animSet = AnimatorSet()
        params.callback?.invoke(animSet)

        val paramMap = params.animMap
        val animList = arrayListOf<Animator>()

        for ((k, param) in paramMap) {
            val animator = if (param.initValue == null) {
                ObjectAnimator.ofFloat(view, k, param.value)
            } else {
                ObjectAnimator.ofFloat(view, k, param.initValue, param.value)
            }

            animator.repeatCount = param.repeatCount
            animator.repeatMode  = param.repeatMode

            animList.add(animator)
        }

        if (animList.size == 0) {
            if (mLog.isDebugEnabled) {
                mLog.debug("ANIMATOR SET == 0")
            }
            return
        }

        params.apply {
            animSet.duration = duration
            interpolator?.let { animSet.interpolator = it }
            startDelay?.let { animSet.startDelay = it }

            animSet.playTogether(animList)

            if (endListener != null) {
                animSet.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator?) {}
                    override fun onAnimationCancel(animator: Animator?) {}
                    override fun onAnimationRepeat(animator: Animator?) {}
                    override fun onAnimationEnd(animator: Animator?) {
                        endListener?.invoke(animator)
                    }
                })
            }

            animSet.start()
        }
    }

    @JvmStatic
    @BindingAdapter("bindTranslateX")
    fun bindTranslateX(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("ANIMATION $K_TRANSLATION_X")
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_X, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_X, params.initValue, params.value)
        }

        objectAnim(view, anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindTranslateY")
    fun bindTranslateY(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("ANIMATION $K_TRANSLATION_Y")
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_Y, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_TRANSLATION_Y, params.initValue, params.value)
        }

        objectAnim(view, anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindAlpha")
    fun bindAlpha(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("ANIMATION $K_ALPHA")
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_ALPHA, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_ALPHA, params.initValue, params.value)
        }

        objectAnim(view, anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindScaleX")
    fun bindScaleX(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("ANIMATION $K_SCALE_X")
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_SCALE_X, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_SCALE_X, params.initValue, params.value)
        }

        objectAnim(view, anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindScaleY")
    fun bindScaleY(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("ANIMATION $K_SCALE_Y")
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_SCALE_Y, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_SCALE_Y, params.initValue, params.value)
        }

        objectAnim(view, anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindRotation")
    fun bindRotation(view: View, params: AnimParams?) {
        if (params == null) {
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("ANIMATION $K_ROTATION")
        }

        val anim = if (params.initValue == null) {
            ObjectAnimator.ofFloat(view, K_ROTATION, params.value)
        } else {
            ObjectAnimator.ofFloat(view, K_ROTATION, params.initValue, params.value)
        }

        objectAnim(view, anim, params)
    }

    @JvmStatic
    @BindingAdapter("bindToLargeAlpha")
    fun bindToLargeAlpha(view: View, params: ToLargeAlphaAnimParams?) {
        params?.apply {
            val anim = view.animate()
                .scaleX(value)
                .scaleY(value)
                .alpha(0f)
                .withEndAction {
                    endListener?.invoke(null)

                    view.apply {
                        scaleX = 1f
                        scaleY = 1f
                        alpha  = 1f

                        if (transX > 0) { translationX = 0f }
                        if (transY > 0) { translationY = 0f }
                    }
                }

            interpolator?.let { anim.setInterpolator(it) }

            if (transX > 0) { anim.translationX(transX) }
            if (transY > 0) { anim.translationY(transY) }

            anim.start()
        }
    }

    private fun objectAnim(view: View, anim: ObjectAnimator, params: AnimParams) {
        params.apply {
            anim.duration = duration
            interpolator?.let { anim.interpolator = it }
            startDelay?.let   { anim.startDelay = it }

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

data class AnimatorSetParams(
    var animMap        : Map<String, AnimatorParams>,
    var duration       : Long = 300,
    var endListener    : ((Animator?) -> Unit)? = null,
    var interpolator   : Interpolator? = null,
    var startDelay     : Long? = null,
    var callback       : ((AnimatorSet?) -> Unit)? = null
)

data class AnimatorParams(
    var value          : Float,
    val initValue      : Float? = null,
    var repeatCount    : Int = 1,
    var repeatMode     : Int = ValueAnimator.RESTART
)

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

data class ToLargeAlphaAnimParams(
    val value : Float,
    val transX : Float = 0f,
    val transY : Float = 0f,
    val duration: Long = 300,
    val endListener : ((Animator?) -> Unit)? = null,
    val interpolator : Interpolator? = null,
    val startDelay : Long? = null
)