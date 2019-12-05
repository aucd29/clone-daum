@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.common.widget

//import android.net.http.SslError
//import android.webkit.ValueCallback
//import brigitte.widget.WebViewSettingParams
//import org.slf4j.LoggerFactory
//import org.xwalk.core.*

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-18 <p/>
 *
 * https://www.mobibrw.com/2015/1934
 *
 * 한번 써볼까 했는데 자료가 다 날라갔네?
 * https://github.com/crosswalk-project/
 *
 * 뒤지다 보니 최신 버전을 찾았네
 * // https://download.01.org/crosswalk/releases/crosswalk/android/maven2/org/xwalk/xwalk_core_library/23.53.589.4/
 */
//
//inline fun XWalkView.defaultSetting(params: WebViewSettingParams) = params.run {
//    // https://github.com/crosswalk-project/crosswalk/blob/master/runtime/android/sample/src/org/xwalk/core/sample/SupportZoomActivity.java
//    settings.apply {
//        textZoom                         = 100
//        cacheMode                        = XWalkSettings.LOAD_DEFAULT
//        javaScriptEnabled                = true
//        domStorageEnabled                = true
//        allowFileAccessFromFileURLs      = true
//        allowUniversalAccessFromFileURLs = true
//
//        allowContentAccess          = false
//        allowFileAccess             = true
//        layoutAlgorithm             = XWalkSettings.LayoutAlgorithm.NARROW_COLUMNS
//        loadsImagesAutomatically    = true
//        useWideViewPort             = true
//
//        userAgent?.invoke().let { userAgentString = it }
//    }
//
//    setResourceClient(object: XWalkResourceClient(this@defaultSetting) {
//        private val mLog = LoggerFactory.getLogger(XWalkView::class.java)
//        var redirectFlag = false
//
//        override fun shouldOverrideUrlLoading(view: XWalkView?, url: String?): Boolean {
//            if (mLog.isDebugEnabled) {
//                mLog.debug("OVERRIDE URL LOADING : $url")
//            }
//
//            urlLoading?.invoke(null, url) ?: view?.loadUrl(url)
//            redirectFlag = true
//
//            return true
//        }
//
//        override fun onLoadStarted(view: XWalkView?, url: String?) {
//            super.onLoadStarted(view, url)
//
//            if (!redirectFlag) {
//                if (mLog.isInfoEnabled) {
//                    mLog.info("PAGE STARTED : $url")
//                }
//
//                view?.let { canGoForward?.invoke(it.canGoForward()) }
//                pageStarted?.invoke(url)
//            }
//
//            redirectFlag = false
//        }
//
//        override fun onLoadFinished(view: XWalkView?, url: String?) {
//            super.onLoadFinished(view, url)
//
//            if (!redirectFlag) {
//                if (mLog.isInfoEnabled) {
//                    mLog.info("PAGE FINISHED")
//                }
//
//                view?.let { canGoForward?.invoke(it.canGoForward()) }
//                pageFinished?.invoke(url)
//            }
//        }
//
//        override fun onReceivedLoadError(view: XWalkView?, errorCode: Int, description: String?, failingUrl: String?) {
//            super.onReceivedLoadError(view, errorCode, description, failingUrl)
//
//            receivedError?.invoke(failingUrl)
//
//            view?.let { canGoForward?.invoke(it.canGoForward()) }
//        }
//
//        override fun onReceivedSslError(view: XWalkView?, callback: ValueCallback<Boolean>?, error: SslError?) {
//            // TODO 이건 수정해야할 듯 null 을 다른걸로 반환해야 함
//            sslError?.invoke(null)
//        }
//
//        override fun onProgressChanged(view: XWalkView?, progressInPercent: Int) {
//            super.onProgressChanged(view, progressInPercent)
//
//            progress?.invoke(progressInPercent)
//        }
//    })
//}
//
//inline fun XWalkView.pause() {
//    pauseTimers()
//    onHide()
//}
//
//inline fun XWalkView.resume() {
//    resumeTimers()
//    onShow()
//}
//
//inline fun XWalkView.free() {
//    onDestroy()
//}
//
//inline fun XWalkView.goBack() {
//    //https://stackoverflow.com/questions/40968633/how-to-use-back-button-to-go-back-with-xwalkview-of-crosswalk-or-disable-it/41093197
//    navigationHistory.navigate(XWalkNavigationHistory.Direction.BACKWARD, 1)
//}
//
//inline fun XWalkView.goForward() {
//    navigationHistory.navigate(XWalkNavigationHistory.Direction.FORWARD, 1)
//}
//
//inline fun XWalkView.canGoBack() =
//    navigationHistory.canGoBack()
//
//inline fun XWalkView.canGoForward() =
//    navigationHistory.canGoForward()
//
//
//
////inline fun XWalkView.loadUrl(url: String) {
////    load(url, null)
////}
//
//inline fun XWalkView.reload() {
//    // https://www.programcreek.com/java-api-examples/?api=org.xwalk.core.XWalkView
//    reload(XWalkView.RELOAD_NORMAL)
//}