package com.example.common.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

object SwipeRefreshLayoutBindingAdapter {
    private val mLog = LoggerFactory.getLogger(SwipeRefreshLayoutBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindSwipeRefreshListener")
    fun bindSwipeRefreshListener(view: SwipeRefreshLayout, callback: () -> Unit) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindSwipeRefreshListener")
        }

        view.setOnRefreshListener(callback)
    }

    @JvmStatic
    @BindingAdapter("bindSwipeRefreshingFalse")
    fun bindSwipeRefreshingFalse(view: SwipeRefreshLayout, dumy: Boolean) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindSwipeRefreshingFalse")
        }

        view.isRefreshing = false
    }

    @JvmStatic
    @BindingAdapter("bindSwipeIsRefreshing")
    fun bindSwipeIsRefreshing(view: SwipeRefreshLayout, callback: ((Boolean) -> Unit)?) {
        callback?.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("bindSwipeIsRefreshing")
            }

            invoke(view.isRefreshing)
        }
    }
}