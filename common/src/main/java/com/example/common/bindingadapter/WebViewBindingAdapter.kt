package com.example.common.bindingadapter

import android.databinding.BindingAdapter
import android.webkit.WebView
import com.example.common.arch.SingleLiveEvent
import com.example.common.defaultSetting
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object WebViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(WebViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindWebViewInit")
    fun bindWebViewInit(webview: WebView, event: SingleLiveEvent<Void>) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindWebViewInit")
        }

        webview.defaultSetting()
        event.call()
    }
}