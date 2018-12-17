package com.example.common.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 17. <p/>
 */

object ViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(ViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindLayoutHeight")
    fun layoutHeight(view: View, height: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bind height")
        }

        val lp = view.layoutParams
        lp.height = height
        view.layoutParams = lp
    }

    @JvmStatic
    @BindingAdapter("bindLayoutWidth")
    fun layoutWidth(view: View, width: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bind width")
        }

        val lp = view.layoutParams
        lp.width = width
        view.layoutParams = lp
    }
}