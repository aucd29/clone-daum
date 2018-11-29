@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.view.Gravity
import android.view.View
import android.widget.TextView

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

inline fun View.height(height: Int) {
    val param = layoutParams
    param.height = height
    layoutParams = param
}

inline fun View.width(width: Int) {
    val param = layoutParams
    param.height = width
    layoutParams = param
}

inline fun TextView.gravityCenter() {
    gravity = Gravity.CENTER
}

inline fun View.dpToPx(v: Int) = v * context.displayDensity()
inline fun View.pxToDp(v: Int) = v / context.displayDensity()