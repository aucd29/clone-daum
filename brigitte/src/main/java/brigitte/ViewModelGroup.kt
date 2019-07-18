@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import brigitte.arch.SingleLiveEvent
import io.reactivex.Single
import org.slf4j.Logger
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-03 <p/>
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

inline fun AndroidViewModel.delay(dealy: Long = 1000, noinline callback: () -> Unit) =
    Single.just("")
        .delay(dealy, TimeUnit.MILLISECONDS)
        .subscribe { d -> callback.invoke() }

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


////////////////////////////////////////////////////////////////////////////////////
//
// CommandEventViewModel
//
////////////////////////////////////////////////////////////////////////////////////

open class CommandEventViewModel(application: Application)
    : AndroidViewModel(application)
    , ICommandEventAware {

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()

    inline fun snackbar(@StringRes resid: Int) = snackbar(string(resid))
    inline fun toast(@StringRes resid: Int) = toast(string(resid))
    inline fun errorLog(e: Throwable, logger: Logger) {
        if (logger.isDebugEnabled) {
            e.printStackTrace()
        }

        logger.error("ERROR: ${e.message}")
    }
}
