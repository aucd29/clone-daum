@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.*
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

inline fun WebView.defaultSetting(noinline urlLoading: ((WebView?, String?) -> Unit)? = null,
                                  noinline pageFinished: ((String?) -> Unit)? = null,
                                  noinline receivedError: ((String?) -> Unit)? = null,
                                  noinline sslError: ((SslErrorHandler?) -> Unit)? = null,
                                  noinline progress: ((Int) -> Unit)? = null) {
    settings.run {
        textZoom = 100
        cacheMode = WebSettings.LOAD_NO_CACHE
        setAppCacheEnabled(true)
        javaScriptEnabled = true
        domStorageEnabled = true
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
    }

    webViewClient = object : WebViewClient() {
        private val mLog = LoggerFactory.getLogger(WebView::class.java)

        var loadingFinished = true
        var redirect = false

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (!loadingFinished) {
                redirect = true
            }

            urlLoading?.invoke(view, url) ?: view?.loadUrl(url)

            return true
        }

        // https://stackoverflow.com/questions/3149216/how-to-listen-for-a-webview-finishing-loading-a-url
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            loadingFinished = false
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
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)

            receivedError?.invoke(failingUrl)
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