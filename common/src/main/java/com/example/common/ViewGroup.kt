@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

inline fun View.layoutHeight(height: Int) = layoutParams.run {
    this.height = height
}

inline fun View.layoutWidth(width: Int) = layoutParams.run {
    this.width = width
}

inline fun TextView.gravityCenter() {
    gravity = Gravity.CENTER
}

inline fun View.dpToPx(v: Int) = v * context.displayDensity()
inline fun View.pxToDp(v: Int) = v / context.displayDensity()

inline fun Int.dpToPx(context: Context) = this * context.displayDensity()
inline fun Int.pxToDp(context: Context) = this / context.displayDensity()