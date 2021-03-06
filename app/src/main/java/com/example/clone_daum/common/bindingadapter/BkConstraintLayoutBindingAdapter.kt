package com.example.clone_daum.common.bindingadapter

import android.view.MotionEvent
import androidx.databinding.BindingAdapter
import brigitte.widget.constraint.ConstraintLayout
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-02 <p/>
 */

object ConstraintLayoutBindingAdapter {
    private val logger = LoggerFactory.getLogger(ConstraintLayoutBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindDispatchTouchEvent")
    fun bindDispatchTouchEvent(view: ConstraintLayout, listener: ((MotionEvent) -> Boolean)?) {
        if (logger.isDebugEnabled) {
            logger.debug("DISPATCH TOUCH EVENT")
        }

        view.dispatchTouchEvent = listener
    }
}