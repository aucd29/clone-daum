@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

/**
 * https://antonioleiva.com/kotlin-ongloballayoutlistener/\
 * https://stackoverflow.com/questions/38827186/what-is-the-difference-between-crossinline-and-noinline-in-kotlin
 */
@Suppress("DEPRECATION")
inline fun View.globalLayoutListener(crossinline f: () -> Boolean) = with (viewTreeObserver) {
    addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (f()) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        }
    })
}

inline fun View.layoutHeight(height: Int) {
    val lp = layoutParams
    lp.height = height

    layoutParams = lp
}

inline fun View.layoutHeight(height: Float) {
    val lp = layoutParams
    lp.height = height.toInt()

    layoutParams = lp
}

inline fun View.layoutWidth(width: Int) {
    val lp = layoutParams
    lp.width = width

    layoutParams = lp
}


inline fun View.layoutWidth(width: Float) {
    val lp = layoutParams
    lp.width = width.toInt()

    layoutParams = lp
}

inline fun TextView.gravityCenter() {
    gravity = Gravity.CENTER
}

inline fun View.showKeyboard(flags: Int = InputMethodManager.SHOW_IMPLICIT) {
    postDelayed({
        requestFocus()
        context.systemService<InputMethodManager>()?.apply {
            showSoftInput(this@showKeyboard, flags) // InputMethodManager.SHOW_FORCED
        }
    }, 200)
}

inline fun View.hideKeyboard() {
    context.systemService<InputMethodManager>()?.apply {
        hideSoftInputFromWindow(this@hideKeyboard.windowToken, 0)
    }
}

inline fun View.dpToPx(v: Float) = v * context.displayDensity()
inline fun View.pxToDp(v: Float) = v / context.displayDensity()
inline fun View.spToPx(v: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, v, context.resources.displayMetrics)

inline fun View.dpToPx(v: Int) = (v * context.displayDensity()).toInt()
inline fun View.pxToDp(v: Int) = (v / context.displayDensity()).toInt()
// https://stackoverflow.com/questions/29664993/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp
inline fun View.spToPx(v: Int) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, v.toFloat(), context.resources.displayMetrics).toInt()
