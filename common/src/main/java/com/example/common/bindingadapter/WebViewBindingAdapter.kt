package com.example.common.bindingadapter

import android.webkit.WebView
import androidx.databinding.BindingAdapter
import com.example.common.WebViewEvent
import com.example.common.WebViewSettingParams
import com.example.common.defaultSetting
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object WebViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(WebViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindWebViewSetting")
    fun bindWebViewSetting(webview: WebView, params: WebViewSettingParams) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindWebViewSetting")
        }

        webview.defaultSetting(params)
    }

    @JvmStatic
    @BindingAdapter("bindWebViewEvent")
    fun bindWebViewEvent(view: WebView, event: WebViewEvent?) = view.run {
        event?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("bindWebViewEvent commandEvent : ${it}")
            }

            when (it) {
                WebViewEvent.FORWARD      -> goForward()
                WebViewEvent.BACK         -> goBack()
                WebViewEvent.STOP_LOADING -> stopLoading()
                WebViewEvent.RELOAD       -> reload()
                WebViewEvent.PAUSE_TIMER  -> pauseTimers()
                WebViewEvent.RESUME_TIMER -> resumeTimers()
                else -> {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("UNKNOWN EVENT")
                    }
                }
            }
        }
    }

//    @JvmStatic
//    @BindingAdapter("bindLoadUrl")
//    fun bindWebViewInit(webview: WebView, url: String) {
//        if (mLog.isDebugEnabled) {
//            mLog.debug("bindLoadUrl")
//        }
//
//        webview.loadUrl(url)
//    }
//
//    @JvmStatic
//    @BindingAdapter("bindTimer")
//    fun bindTimer(webview: WebView, pause: Boolean) = webview.run {
//        if (pause) {
//            pauseTimers()
//        } else {
//            resumeTimers()
//        }
//    }
//
//    @JvmStatic
//    @BindingAdapter("bindReload")
//    fun bindReload(webview: WebView, reload: Boolean) = webview.run {
//        reload()
//    }
//
//    @JvmStatic
//    @BindingAdapter("bindForward")
//    fun bindForward(webview: WebView, reload: Boolean) = webview.run {
//        goForward()
//    }
}
