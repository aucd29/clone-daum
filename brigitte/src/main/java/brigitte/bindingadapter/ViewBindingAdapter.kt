package brigitte.bindingadapter

import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.databinding.BindingAdapter
import brigitte.layoutHeight
import brigitte.layoutWidth
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 17. <p/>
 */

object ViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(ViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindLayoutHeight")
    fun layoutHeight(view: View, height: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bind height : $height")
        }

        view.layoutHeight(height)
    }

    @JvmStatic
    @BindingAdapter("bindLayoutWidth")
    fun layoutWidth(view: View, width: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bind width : $width")
        }

        view.layoutWidth(width)
    }

    @JvmStatic
    @BindingAdapter("bindVideoUrl")
    fun bindVideoUrl(view: VideoView, url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND VIDEO URL : $url")
        }

        view.setVideoURI(Uri.parse(url))
    }

    @JvmStatic
    @BindingAdapter("bindAlphaValue")
    fun bindVideoUrl(view: View, alpha: Float) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND ALPHA VALUE : $alpha")
        }

        view.alpha = alpha
    }

    @JvmStatic
    @BindingAdapter("bindTranslationYValue")
    fun bindTranslationYValue(view: View, transY: Float) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND TRANSLATION Y VALUE : $transY")
        }

        view.translationY = transY
    }

}