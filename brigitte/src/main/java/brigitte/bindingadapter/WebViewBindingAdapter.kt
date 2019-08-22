package brigitte.bindingadapter

import android.webkit.WebView
import androidx.databinding.BindingAdapter
import brigitte.widget.WebViewEvent
import brigitte.widget.pause
import brigitte.widget.resume
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object WebViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(WebViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindWebViewEvent")
    fun bindWebViewEvent(view: WebView, event: WebViewEvent?) = view.run {
        // 잠시 잊고 있었다. =_ = observable 에 동일한 값이 들어오면 무시하는걸.. =_ =;

        event?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("bindWebViewEvent : $it")
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
}
