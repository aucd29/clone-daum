@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 1. <p/>
 */

typealias AniListener = (Boolean, Animator?) -> Unit

/**
 * view 의 높이 값을 변경 한다.
 *
 * @param 변경할 높이
 * @param aniListener animation 진행 상황을 알리는 listener
 * @param duration animation 진행 시간 (기본 값 : 600)
 */
inline fun View.aniHeight(height: Int, noinline aniListener: AniListener? = null, duration:Long = 600) {
    Resize.height(this, height, aniListener, duration)
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
        override fun onAnimationStart(p0: Animator?) = start(p0)
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) = end(animation)
    })
}