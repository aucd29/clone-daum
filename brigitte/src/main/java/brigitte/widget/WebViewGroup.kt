@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.widget

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import brigitte.BaseDaggerFragment
import org.slf4j.LoggerFactory
import java.lang.Exception

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 *
 * https://github.com/aosp-mirror/platform_frameworks_base/tree/master/core/java/android/webkit
 */

@SuppressLint("SetJavaScriptEnabled")
inline fun WebView.defaultSetting(params: WebViewSettingParams) = params.run {
    settings.apply {
        textZoom                         = 100
//        cacheMode                        = WebSettings.LOAD_NO_CACHE
        javaScriptEnabled                = true
        domStorageEnabled                = true
        allowFileAccessFromFileURLs      = true
        allowUniversalAccessFromFileURLs = true

        setAppCacheEnabled(true)
        setAppCachePath(context.cacheDir.absolutePath)
        setNeedInitialFocus(false)

        allowContentAccess          = false
        allowFileAccess             = true
        cacheMode                   = WebSettings.LOAD_DEFAULT
        layoutAlgorithm             = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        loadsImagesAutomatically    = true
        minimumFontSize             = 8
        minimumLogicalFontSize      = 8
        useWideViewPort             = true

        //https://stackoverflow.com/questions/21694306/how-to-set-text-size-in-webview-in-android
//        blockNetworkImage                = true
//        loadsImagesAutomatically         = true

//        https://blog.csdn.net/cjopengler/article/details/17099181     // 동작 안함 [aucd29][2019. 3. 5.]
//        Reflect.method(settings, "setPageCacheCapacity",
//            Reflect.Params(listOf(Integer::class.java), listOf(5)))

        userAgent?.invoke().let { userAgentString = it }
    }

    webViewClient = object : WebViewClient() {
        private val mLog = LoggerFactory.getLogger(WebView::class.java)
        var redirectFlag = false

        override fun shouldOverrideUrlLoading(view: WebView?, loadingUrl: String?): Boolean {
            if (mLog.isDebugEnabled) {
                mLog.debug("URL LOADING #1($this) : $loadingUrl")
            }

            urlLoading?.invoke(view, loadingUrl) ?: view?.loadUrl(loadingUrl)
            redirectFlag = true

            return true
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (mLog.isDebugEnabled) {
                mLog.debug("URL LOADING #2($this, $view) : ${request?.url.toString()}")
            }

            request?.let { r ->
                urlLoading?.invoke(view, r.url.toString()) ?: view?.loadUrl(r.url.toString())
            }

            redirectFlag = true

            return true
        }

        // https://stackoverflow.com/questions/3149216/how-to-listen-for-a-webview-finishing-loading-a-url
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            if (!redirectFlag) {
                if (mLog.isInfoEnabled) {
                    mLog.info("PAGE STARTED : $url")
                }

                view?.let { canGoForward?.invoke(it.canGoForward()) }
                pageStarted?.invoke(url)
            }

            redirectFlag = false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            // 어래 수정한다고 했는데 callback 이 2번 나가서 다시 수정 =_ = [aucd29][2019. 4. 17.]
            if (!redirectFlag) {
                if (mLog.isInfoEnabled) {
                    mLog.info("PAGE FINISHED")
                }

                view?.let { canGoForward?.invoke(it.canGoForward()) }
                pageFinished?.invoke(url)
            }
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
        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            // FIXME 변경 필요
            return super.onJsAlert(view, url, message, result)
        }

        override fun onJsConfirm(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            // FIXME 변경 필요
            return super.onJsConfirm(view, url, message, result)
        }

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

data class WebViewSettingParams constructor (
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
