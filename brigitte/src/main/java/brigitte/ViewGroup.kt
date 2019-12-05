@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.graphics.*
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

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
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                    viewTreeObserver.removeOnGlobalLayoutListener(this)
//                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
//                }
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

inline fun View.rawXY(root: ViewGroup): Rect {
    val rect = Rect()
    getDrawingRect(rect)
    root.offsetDescendantRectToMyCoords(this, rect)

    return rect
}

inline fun View.displayXY(): IntArray {
    val raw = IntArray(2)
    getLocationInWindow(raw)

    return raw
}

inline fun TextView.gravityCenter() {
    gravity = Gravity.CENTER
}

inline fun TextView.bold() {
    setTypeface(typeface, Typeface.BOLD)
}

inline fun TextView.normal() {
    setTypeface(typeface, Typeface.NORMAL)
}

inline fun TextView.boldById(id: Int) {
    if (this.id == id) {
        bold()
    } else {
        normal()
    }
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


////////////////////////////////////////////////////////////////////////////////////
//
// ScrollChangeListener
//
////////////////////////////////////////////////////////////////////////////////////

// https://stackoverflow.com/questions/39894792/recyclerview-scrolllistener-inside-nestedscrollview
class ScrollChangeListener constructor (
    val callback: (Int, Int, Boolean) -> Unit
) : NestedScrollView.OnScrollChangeListener {
    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        callback.invoke(scrollX, scrollY, v?.run {
            val res = scrollY == (getChildAt(0).measuredHeight - measuredHeight)
            res
        } ?: false)
    }
}


////////////////////////////////////////////////////////////////////////////////////
//
// CAPTURE
//
////////////////////////////////////////////////////////////////////////////////////

data class CaptureParams constructor (
    val fileName: String,
    val listener: ((Boolean, String) -> Unit)? = null,
    val path: String? = null,
    val quality: Int = 90,
    val waterMark: Bitmap? = null
)

inline fun View.capture(params: CaptureParams): Single<Pair<Boolean, File?>> = with(params) {
    val PATH_SCREENSHOTS = "Screenshots"
    var buffer: Bitmap? = null
    var canvas: Canvas? = null

    Single.just(this)
        .subscribeOn(AndroidSchedulers.mainThread())
        .map {
            try {
                clearFocus()
                isPressed = false
                invalidate()

                buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                buffer?.let {
                    canvas = Canvas(it)
                    draw(canvas)
                }

                true
            } catch (e: IOException) {
                false
            }
        }
        .subscribeOn(Schedulers.io())
        .map {
            if (!it) {
                return@map false to null
            }

            if (waterMark != null) {
                val offsetx = width - waterMark.width
                val offsety = height - waterMark.height

                canvas?.drawBitmap(waterMark, offsetx.toFloat(), offsety.toFloat(), Paint())
            }

            // FIXME 29 버전에는 scoped storage 때문에 문제 존재 가능
            val savedPath = File(path?.let { File(it) } ?: context.dcim(),
                PATH_SCREENSHOTS)
            if (!savedPath.exists()) {
                savedPath.mkdirs()
            }

            try {
                val bmpfp = File(savedPath, fileName)
                FileOutputStream(bmpfp).use {
                    buffer?.compress(Bitmap.CompressFormat.PNG, params.quality, it)
                }

                true to bmpfp
            } catch (e: Exception) {
                false to null
            } finally {
                buffer?.recycle()
            }
        }
        .observeOn(AndroidSchedulers.mainThread())
}


