@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 8. <p/>
 */


// LinearLayout

inline fun LinearLayout.lp(w: Int, h: Int) {
    layoutParams = LinearLayout.LayoutParams(w, h)
}

inline fun LinearLayout.lpmm() {
    layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
}

inline fun LinearLayout.lpwm() {
    layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
}

inline fun LinearLayout.lpmw() {
    layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
}

inline fun LinearLayout.lpww() {
    layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
}

// RelativeLayout

inline fun RelativeLayout.lp(w: Int, h: Int) {
    layoutParams = RelativeLayout.LayoutParams(w, h)
}

inline fun RelativeLayout.lpmm() {
    layoutParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
}

inline fun RelativeLayout.lpwm() {
    layoutParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT)
}

inline fun RelativeLayout.lpmw() {
    layoutParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
}

inline fun RelativeLayout.lpww() {
    layoutParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
}

// FrameLayout

inline fun FrameLayout.lp(w: Int, h: Int) {
    layoutParams = FrameLayout.LayoutParams(w, h)
}

inline fun FrameLayout.lpmm() {
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
}

inline fun FrameLayout.lpwm() {
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
}

inline fun FrameLayout.lpmw() {
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
}

inline fun FrameLayout.lpww() {
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
}

// ViewGroup

inline fun ViewGroup.lp(w: Int, h: Int) {
    layoutParams = ViewGroup.LayoutParams(w, h)
}

inline fun ViewGroup.lpmm() {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
}

inline fun ViewGroup.lpwm() {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
}

inline fun ViewGroup.lpmw() {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
}

inline fun ViewGroup.lpww() {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
}

// Window

inline fun Window.lp(w: Int, h: Int) {
    val lp = attributes
    lp.width = w
    lp.height = h

    attributes = lp
}

inline fun Window.lpmm() {
    attributes = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
}

inline fun Window.lpwm() {
    attributes = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT)
}

inline fun Window.lpmw() {
    attributes = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
}

inline fun Window.lpww() {
    attributes = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
}

// Constraint
//
//inline fun ConstraintLayout.lp(w: Int, h: Int) {
//    layoutParams = ConstraintLayout.LayoutParams(w, h)
//}
//
inline fun ConstraintLayout.lpmm(applyTo: ConstraintLayout) {
    ConstraintSet().apply {
        connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
        connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
        connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

        applyTo(applyTo)
    }
}

//
//inline fun ConstraintLayout.lpmw() {
//    layoutParams = ConstraintLayout.LayoutParams(
//        ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//}
//
//inline fun ConstraintLayout.lpwm() {
//    layoutParams = ConstraintLayout.LayoutParams(
//        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
//}
//
//inline fun ConstraintLayout.lpww() {
//    layoutParams = ConstraintLayout.LayoutParams(
//        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//}