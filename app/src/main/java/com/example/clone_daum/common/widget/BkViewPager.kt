package com.example.clone_daum.common.widget

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 11. <p/>
 */

class BkViewPager: ViewPager {
    constructor(context: Context) : super(context) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initLayout()
    }

    fun initLayout() {

    }

    // HEIGHT WRAP_CONTENT
    // https://stackoverflow.com/questions/8394681/android-i-am-unable-to-have-viewpager-wrap-content
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        var height = 0
//        var i = 0
//        val count = childCount
//
//        while (i < count) {
//            val child = getChildAt(i)
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
//
//            val h = child.measuredHeight
//            if (h > height) height = h
//
//            ++i
//        }
//
//        val newHeight = if (height != 0) {
//            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
//        } else {
//            heightMeasureSpec
//        }
//
//        super.onMeasure(widthMeasureSpec, newHeight)

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val firstChild = getChildAt(0)
        firstChild?.let {
            it.measure(widthMeasureSpec, heightMeasureSpec)

            super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(it.getMeasuredHeight(), MeasureSpec.EXACTLY))
        }
    }
}