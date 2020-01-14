package brigitte.bindingadapter

import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import brigitte.ScrollChangeListener
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-06-29 <p/>
 */

object ScrollViewBindingAdapter {
    private val logger = LoggerFactory.getLogger(ScrollViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindScrollChangeListener")
    fun bindScrollChangeListener(view: NestedScrollView, listener: ScrollChangeListener? = null) {
        if (logger.isDebugEnabled) {
            logger.debug("BIND SCROLLVIEW LISTENER")
        }

        listener?.let { view.setOnScrollChangeListener(it) }
    }

}