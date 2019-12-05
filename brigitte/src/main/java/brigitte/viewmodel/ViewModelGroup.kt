@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import brigitte.displayDensity
import brigitte.string

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-03 <p/>
 */

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(@StringRes resid: Int): String =
    app.getString(resid)

inline fun AndroidViewModel.stringArray(@ArrayRes resid: Int): Array<String> =
    app.resources.getStringArray(resid)

inline fun AndroidViewModel.intArray(@ArrayRes resid: Int): IntArray =
    app.resources.getIntArray(resid)

inline fun AndroidViewModel.requireContext(): Context =
    if (app == null) {
        throw IllegalStateException("AndroidViewModel $this not attached to a context.")
    } else {
        app.applicationContext
    }

inline fun AndroidViewModel.color(@ColorRes resid: Int) =
    ContextCompat.getColor(app, resid)

inline fun AndroidViewModel.drawable(str: String): Drawable? {
    val id = app.resources.getIdentifier(str, "drawable", app.packageName)
    return ContextCompat.getDrawable(app, id)
}

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(resid: String): String = app.string(resid)

inline val AndroidViewModel.app : Application
    get() = getApplication()

inline fun AndroidViewModel.time() = System.currentTimeMillis()

inline fun AndroidViewModel.dpToPx(v: Int) = dpToPx(v.toFloat()).toInt()
inline fun AndroidViewModel.pxToDp(v: Int) = pxToDp(v.toFloat()).toInt()
inline fun AndroidViewModel.dpToPx(v: Float) = v * app.displayDensity()
inline fun AndroidViewModel.pxToDp(v: Float) = v / app.displayDensity()

