package brigitte.bindingadapter

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import brigitte.TabSelectedCallback
import brigitte.arch.SingleLiveEvent
import com.google.android.material.tabs.TabLayout
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object TabLayoutBindingAdapter {
    private val logger = LoggerFactory.getLogger(TabLayoutBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindSetupWithViewPager", "bindTabLoaded", requireAll = false)
    fun bindSetupWithViewPager(tab: TabLayout, viewpager: ViewPager?, tabLoadedCallback: (() -> Unit)? = null) {
        viewpager?.let {
            if (logger.isDebugEnabled) {
                logger.debug("bindSetupWithViewPager")
            }

            tab.setupWithViewPager(it)
            tabLoadedCallback?.invoke()
        }
    }

    @JvmStatic
    @BindingAdapter("bindTabSelect")
    fun bindTabSelect(tab: TabLayout, index: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("bindTabSelect $index")
        }

        if (index < 0 || tab.tabCount < index) {
            logger.error("ERROR: INVALID INDEX ($index)")

            return
        }

        tab.getTabAt(index)?.select()
    }

    @JvmStatic
    @BindingAdapter("bindTabChanged")
    fun bindTabChanged(tab: TabLayout, tabSelectedCallback: TabSelectedCallback) {
        if (logger.isDebugEnabled) {
            logger.debug("ADD TAB SELECTED LISTENER")
        }
        tab.addOnTabSelectedListener(tabSelectedCallback)
    }

    @JvmStatic
    @BindingAdapter("bindIndicatorColor")
    fun bindIndicatorColor(tab: TabLayout, @ColorInt color: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("BIND TAB INDICATOR COLOR")
        }

        tab.setSelectedTabIndicatorColor(color)
    }
}