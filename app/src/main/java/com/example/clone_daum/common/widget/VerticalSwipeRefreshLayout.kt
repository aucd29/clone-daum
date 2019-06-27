package com.example.clone_daum.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 26. <p/>
 *
 * https://stackoverflow.com/questions/34136178/swiperefreshlayout-blocking-horizontally-scrolled-recyclerview
 */

class VerticalSwipeRefreshLayout(context: Context, attrs: AttributeSet) : SwipeRefreshLayout(context, attrs) {
    private var touchSlop: Int
    private var prevX: Float = 0f
    private var decliend: Boolean = false

    init {
        this.initLayout()
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    open fun initLayout() {

    }

    @SuppressLint("Recycle")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                prevX = MotionEvent.obtain(ev).x
                decliend = false
            }
            MotionEvent.ACTION_MOVE -> {
                val evX = ev.x
                val xDiff = abs(evX - prevX)

                if (decliend || xDiff > touchSlop) {
                    decliend = true

                    return false
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }
}