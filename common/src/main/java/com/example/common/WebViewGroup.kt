@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.databinding.ObservableField
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import java.lang.Exception

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

@SuppressLint("SetJavaScriptEnabled")
inline fun WebView.defaultSetting(params: WebViewSettingParams) = params.run {
    settings.apply {
        setAppCacheEnabled(true)
        textZoom                         = 100
        cacheMode                        = WebSettings.LOAD_NO_CACHE
        javaScriptEnabled                = true
        domStorageEnabled                = true
        allowFileAccessFromFileURLs      = true
        allowUniversalAccessFromFileURLs = true
        userAgent?.invoke().let { userAgentString = it }
    }

    webViewClient = object : WebViewClient() {
        private val mLog = LoggerFactory.getLogger(WebView::class.java)

        var loadingFinished = true
        var redirect        = false

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (!loadingFinished) {
                redirect = true
            }

            if (mLog.isDebugEnabled) {
                mLog.debug("shouldOverrideUrlLoading : $url")
            }

            urlLoading?.invoke(view, url) ?: view?.loadUrl(url)

            return true
        }

        // https://stackoverflow.com/questions/3149216/how-to-listen-for-a-webview-finishing-loading-a-url
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            loadingFinished = false
            view?.let { canGoForward?.invoke(it.canGoForward()) }
            url?.let  { pageStarted?.invoke(it) }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (!redirect) {
                loadingFinished = true
            }

            if (loadingFinished && !redirect) {
                pageFinished?.invoke(url)
            } else {
                redirect = false
            }

            view?.let { canGoForward?.invoke(it.canGoForward()) }
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)

            receivedError?.invoke(failingUrl)

            view?.let { canGoForward?.invoke(it.canGoForward()) }
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            //super.onReceivedSslError(view, handler, error)
            // http://theeye.pe.kr/archives/2721
            sslError?.invoke(handler)
        }
    }

    webChromeClient = object: WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progress?.invoke(newProgress)
        }
    }
}

inline fun WebView.pause() {
    pauseTimers()
    onPause()
}

inline fun WebView.resume() {
    resumeTimers()
    onResume()
}

inline fun WebView.free() {
    try {
        webViewClient = null

        clearHistory()
        removeAllViews()

        destroyDrawingCache()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            freeMemory()
        }
    } catch (ignored: Exception) {
    }
}

data class WebViewSettingParams (
    val urlLoading      : ((WebView?, String?) -> Unit)? = null,
    val pageFinished    : ((String?) -> Unit)? = null,
    val pageStarted     : ((String?) -> Unit)? = null,
    val receivedError   : ((String?) -> Unit)? = null,
    val sslError        : ((SslErrorHandler?) -> Unit)? = null,
    val progress        : ((Int) -> Unit)? = null,
    val canGoForward    : ((Boolean) -> Unit)? = null,
    val userAgent       : (() -> String)? = null
)

enum class WebViewEvent {
    RELOAD, BACK, FORWARD, STOP_LOADING, PAUSE, RESUME
}

interface IWebViewEventAware {
    val webviewEvent: ObservableField<WebViewEvent>

    fun webviewEvent(event: WebViewEvent) {
        webviewEvent.apply {
            if (get() == event) {
                notifyChange()
            } else {
                set(event)
            }
        }
    }
}