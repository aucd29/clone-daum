package com.example.clone_daum.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 28. <p/>
 */

class BkConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    companion object {
        private val mLog = LoggerFactory.getLogger(BkConstraintLayout::class.java)
    }

    // https://stackoverflow.com/questions/13283827/onintercepttouchevent-only-gets-action-down/13540006
    var dispatchTouchEvent : ((Int) -> Boolean)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mLog.isTraceEnabled) {
            mLog.trace("dispatchTouchEvent : ${ev.action}")
        }

        dispatchTouchEvent?.let {
            if (it.invoke(ev.action)) {
                return true
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}