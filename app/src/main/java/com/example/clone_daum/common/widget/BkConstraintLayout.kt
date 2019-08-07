@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 28. <p/>
 *
 * touch intercept 를 목적으로 한 커스텀
 */

class BkConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    companion object {
        private val mLog = LoggerFactory.getLogger(BkConstraintLayout::class.java)
    }

    var dispatchTouchEvent : ((MotionEvent) -> Boolean)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mLog.isTraceEnabled) {
            mLog.trace("dispatchTouchEvent : ${ev.action}")
        }

        dispatchTouchEvent?.let {
            if (it.invoke(ev)) {
                return true
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}

inline fun magneticEffect(event: MotionEvent, y: Int, max: Int, callback: (Boolean) -> Unit): Boolean {
    val mid = max / 2

    return when (event.action) {
        MotionEvent.ACTION_UP -> {
            val result = when (y) {
                in mid..-1 -> {
                    callback.invoke(true)
                    true
                }
                in (max + 1) until mid -> {
                    callback.invoke(false)
                    true
                }
                else -> false
            }

            result
        }
        else -> false
    }
}