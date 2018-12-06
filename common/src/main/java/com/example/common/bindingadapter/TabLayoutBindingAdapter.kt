package com.example.common.bindingadapter

import android.databinding.BindingAdapter
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
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