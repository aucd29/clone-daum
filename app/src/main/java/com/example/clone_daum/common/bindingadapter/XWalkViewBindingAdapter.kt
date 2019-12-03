package com.example.clone_daum.common.bindingadapter

//import androidx.databinding.BindingAdapter
//import brigitte.widget.WebViewEvent
//import com.example.clone_daum.common.widget.*
//import org.slf4j.LoggerFactory
//import org.xwalk.core.XWalkView

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-18 <p/>
 */

//object XWalkViewBindingAdapter {
//    private val mLog = LoggerFactory.getLogger(XWalkViewBindingAdapter::class.java)
//
//    @JvmStatic
//    @BindingAdapter("bindWebViewEvent")
//    fun bindWebViewEvent(view: XWalkView, event: WebViewEvent?) = view.run {
//        event?.let {
//            if (mLog.isDebugEnabled) {
//                mLog.debug("bindWebViewEvent : $it")
//            }
//
//            when (it) {
//                WebViewEvent.FORWARD      -> goForward()
//                WebViewEvent.BACK         -> goBack()
//                WebViewEvent.RELOAD       -> reload()
//                WebViewEvent.PAUSE        -> pause()
//                WebViewEvent.RESUME       -> resume()
//                WebViewEvent.STOP_LOADING -> stopLoading()
//                else -> {
//                    if (mLog.isDebugEnabled) {
//                        mLog.debug("UNKNOWN EVENT")
//                    }
//                }
//            }
//        }
//    }
//}