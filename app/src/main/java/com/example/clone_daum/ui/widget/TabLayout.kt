package com.example.clone_daum.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.clone_daum.R

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 28. <p/>
 */

class TabLayout : com.google.android.material.tabs.TabLayout {
    val paint = Paint()

    constructor(context: Context) : super(context) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.initLayout()
    }

    fun initLayout() {
        paint.run {
            color = ContextCompat.getColor(context, R.color.tab_backgound)
            style = Paint.Style.FILL
            strokeWidth = context.dptoPixel(10)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawLine(0f, measuredHeight.toFloat(), measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
    }
}