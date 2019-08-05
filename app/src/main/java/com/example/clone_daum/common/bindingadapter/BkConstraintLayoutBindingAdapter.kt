package com.example.clone_daum.common.bindingadapter

import android.view.MotionEvent
import androidx.databinding.BindingAdapter
import com.example.clone_daum.common.widget.BkConstraintLayout
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-02 <p/>
 */

object BkConstraintLayoutBindingAdapter {
    private val mLog = LoggerFactory.getLogger(BkConstraintLayoutBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindDispatchTouchEvent")
    fun dispatchTouchEvent(view: BkConstraintLayout, listener: ((MotionEvent) -> Boolean)?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("DISPATCH TOUCH EVENT")
        }

        view.dispatchTouchEvent = listener
    }

}