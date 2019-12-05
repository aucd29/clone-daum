@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewPropertyAnimatorListener
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 1. <p/>
 */

typealias AniListener = (Boolean, Animator?) -> Unit

interface ViewPropertyEndAnimatorListener: ViewPropertyAnimatorListener {
    override fun onAnimationCancel(view: View?) { }
    override fun onAnimationStart(view: View?) { }
}


/**
 * view 의 높이 값을 변경 한다.
 *
 * @param height 변경할 높이
 * @param aniListener animation 진행 상황을 알리는 listener
 * @param duration animation 진행 시간 (기본 값 : 600)
 */
inline fun View.aniHeight(height: Int, noinline aniListener: AniListener? = null, duration:Long = 600) {
    Resize.height(this, height, aniListener, duration)
}

inline fun View.fadeColor(fcolor: Int, scolor: Int, noinline aniListener: AniListener? = null, duration: Long = 600) {
    FadeColor.start(this, fcolor, scolor, aniListener, duration)
}

inline fun View.fadeColorResource(@ColorRes fcolor: Int, @ColorRes scolor: Int,
                                  noinline aniListener: AniListener? = null, duration: Long = 600) {
    FadeColor.startResource(this, fcolor, scolor, aniListener, duration)
}

object Resize {
    private val mLog = LoggerFactory.getLogger(Resize::class.java)

    fun height(view: View, height: Int, aniListener: AniListener? = null, duration: Long = 600) {
        ValueAnimator.ofInt(view.measuredHeight, height).apply {
            addUpdateListener {
                view.layoutHeight(animatedValue as Int)
            }

            aniListener({
                if (view.visibility != View.VISIBLE) {
                    view.visibility = View.VISIBLE
                }

                aniListener?.invoke(true, it)
            }, {
                removeAllUpdateListeners()
                removeAllListeners()

                if (height == 0) {
                    view.visibility = View.GONE
                } else {
                    view.visibility = View.VISIBLE
                }

                aniListener?.invoke(false, it)
            })

            this.duration = duration
        }.start()
    }
}

object FadeColor {
    fun start(view: View, fcolor: Int, scolor: Int, f: AniListener? = null, duration: Long = 500) {
        ObjectAnimator.ofObject(view, "backgroundColor", ArgbEvaluator(), fcolor, scolor).apply {
            this.duration = duration
            aniEndListener {
                f?.invoke(true, it)
            }

            this.duration = duration
        }.start()
    }

    fun startResource(view: View, @ColorRes fcolor: Int, @ColorRes scolor: Int,
                      f: AniListener? = null, duration: Long = 500) {
        start(view, ContextCompat.getColor(view.context, fcolor), ContextCompat.getColor(view.context, scolor), f, duration)
    }
}

/**
 * Animator.AnimatorListener 중 onAnimationEnd 만 전달 받기 위한 리스너
 */
inline fun Animator.aniEndListener(crossinline f: (Animator?) -> Unit) {
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) = f(animation)
    })
}

/**
 * Animator.AnimatorListener 중 onAnimationStart 와 onAnimationEnd 를 전달 받기 위한 리스너
 */
inline fun Animator.aniListener(crossinline start: (Animator?) -> Unit, crossinline end: (Animator?) -> Unit) {
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) = start(animation)
        override fun onAnimationEnd(animation: Animator?) = end(animation)
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}
    })
}