package com.example.common.bindingadapter

import android.webkit.WebView
import androidx.databinding.BindingAdapter
import com.example.common.*
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object WebViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(WebViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindWebViewEvent")
    fun bindWebViewEvent(view: WebView, event: WebViewEvent?) = view.run {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindWebViewEvent $event")
        }

        // 잠시 잊고 있었다. =_ = observable 에 동일한 값이 들어오면 무시하는걸.. =_ =;

        event?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("bindWebViewEvent : ${it}")
            }

            when (it) {
                WebViewEvent.FORWARD      -> goForward()
                WebViewEvent.BACK         -> goBack()
                WebViewEvent.RELOAD       -> reload()
                WebViewEvent.PAUSE        -> pause()
                WebViewEvent.RESUME       -> resume()
                WebViewEvent.STOP_LOADING -> stopLoading()
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
