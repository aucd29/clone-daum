package com.example.common.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentPagerAdapter
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
            mLog.debug("bindOffscreenPageLimit")
        }
        viewpager.offscreenPageLimit = limit
    }

    @JvmStatic
    @BindingAdapter("bindPagerAdapter")
    fun bindAdapter(viewpager: ViewPager, adapter: FragmentPagerAdapter) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindPagerAdapter")
        }
        viewpager.adapter = adapter
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