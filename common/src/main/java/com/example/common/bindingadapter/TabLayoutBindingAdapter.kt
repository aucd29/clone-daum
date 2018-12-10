package com.example.common.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import com.example.common.arch.SingleLiveEvent
import com.google.android.material.tabs.TabLayout
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object TabLayoutBindingAdapter {
    private val mLog = LoggerFactory.getLogger(TabLayoutBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter(*arrayOf("bindSetupWithViewPager", "bindTabLoaded"), requireAll = false)
    fun bindSetupWithViewPager(tab: TabLayout, viewpager: ViewPager, tabLoadedCallback: (() -> Unit)? = null) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindSetupWithViewPager")
        }

        tab.setupWithViewPager(viewpager)
        tabLoadedCallback?.invoke()
    }

    @JvmStatic
    @BindingAdapter("bindTabSelect")
    fun bindTabSelect(tab: TabLayout, index: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindTabSelect $index")
        }

        if (index < 0 || tab.tabCount < index) {
            mLog.error("ERROR: INVALID INDEX ($index)")

            return
        }

        tab.getTabAt(index)?.select()
    }
}