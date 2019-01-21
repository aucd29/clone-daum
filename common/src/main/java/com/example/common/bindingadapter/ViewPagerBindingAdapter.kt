package com.example.common.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object ViewPagerBindingAdapter {
    private val mLog = LoggerFactory.getLogger(ViewPagerBindingAdapter::class.java)
//    @JvmStatic
//    @InverseBindingAdapter(attribute = "currentItem")
//    fun currentItem(viewpager: ViewPager) {
//        viewpager.currentItem
//    }

    @JvmStatic
    @BindingAdapter("bindOffscreenPageLimit")
    fun bindOffscreenPageLimit(viewpager: ViewPager, limit: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindOffscreenPageLimit : $limit")
        }
        viewpager.offscreenPageLimit = limit
    }

    @JvmStatic
    @BindingAdapter(*arrayOf("bindPagerAdapter", "bindViewPagerLoaded"), requireAll = false)
    fun bindAdapter(viewpager: ViewPager, adapter: PagerAdapter, viewPagerLoadedCallback: (() -> Unit)? = null) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindPagerAdapter")
        }

        viewpager.adapter = adapter
        viewPagerLoadedCallback?.invoke()
    }

    @JvmStatic
    @BindingAdapter("bindPageChangeListener")
    fun bindPageChangeListener(viewpager: ViewPager, listener: ViewPager.OnPageChangeListener) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindPageChangeListener")
        }
        viewpager.addOnPageChangeListener(listener)
    }
}