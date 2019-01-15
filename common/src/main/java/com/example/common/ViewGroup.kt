@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

/**
 * https://antonioleiva.com/kotlin-ongloballayoutlistener/\
 * https://stackoverflow.com/questions/38827186/what-is-the-difference-between-crossinline-and-noinline-in-kotlin
 */
@Suppress("DEPRECATION")
inline fun View.layoutListener(crossinline f: () -> Unit) = with (viewTreeObserver) {
    addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            } else {
                viewTreeObserver.removeGlobalOnLayoutListener(this)
            }

            f()
        }
    })
}

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

// https://stackoverflow.com/questions/29664993/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp

inline fun View.spToPx(v: Int) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, v.toFloat(), context.resources.displayMetrics)

inline fun Int.spToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, this.toFloat(), context.resources.displayMetrics)