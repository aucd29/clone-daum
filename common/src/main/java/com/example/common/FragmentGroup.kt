@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.example.common.arch.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 8. <p/>
 */

inline fun Fragment.dpToPx(v: Float) = v * context!!.displayDensity()
inline fun Fragment.pxToDp(v: Float) = v / context!!.displayDensity()
inline fun Fragment.dpToPx(v: Int) = dpToPx(v.toFloat()).toInt()
inline fun Fragment.pxToDp(v: Int) = pxToDp(v.toFloat()).toInt()


inline fun Fragment.string(@StringRes resid: Int): String? = context?.string(resid)

//inline val FragmentManager.childList: List<Fragment?>?
//    get() = (0..backStackEntryCount - 1).map { findFragmentByTag(getBackStackEntryAt(it).name) }

inline val FragmentManager.current: Fragment?
    get() = fragments.last()

inline val FragmentManager.count: Int
    get() = backStackEntryCount

inline fun FragmentManager.find(clazz: Class<out Fragment>) = findFragmentByTag(clazz.name)

inline fun Fragment.observeDialog(event: SingleLiveEvent<DialogParam>, disposable: CompositeDisposable? = null) {
    event.observe(this, Observer { dialog(it, disposable) })
}

inline fun Fragment.snackbar(view: View, msg: String, length: Int = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT) =
    activity?.snackbar(view, msg, length)

inline fun Fragment.snackbar(view: View, @StringRes resid: Int, length: Int = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT) =
    activity?.snackbar(view, resid, length)

inline fun Fragment.dialog(params: DialogParam, disposable: CompositeDisposable? = null) {
    activity?.dialog(params, disposable)
}

inline fun Fragment.alert(messageId: Int, titleId: Int? = null) {
    dialog(DialogParam(context = context
        , messageId = messageId
        , titleId   = titleId))
}

inline fun Fragment.confirm(messageId: Int, titleId: Int? = null
                            , noinline listener: ((Boolean, DialogInterface) -> Unit)? = null) {
    dialog(DialogParam(context = context
        , messageId  = messageId
        , titleId    = titleId
        , negativeId = android.R.string.cancel
        , listener   = listener))
}

inline fun Fragment.hideKeyboard(view: View) =
    activity?.hideKeyboard(view)

inline fun Fragment.finish() =
    fragmentManager?.pop()

inline fun Fragment.keepScreen(on: Boolean) {
    activity?.keepScreen(on)
}

object FragmentAnim {
    val LEFT: String
        get() = "left"

    val RIGHT: String
        get() = "right"

    val UP: String
        get() = "up"

    val ALPHA: String
        get() = "alpha"
}

object FragmentCommit {
    val NOW: String
        get() = "now"

    val ALLOW: String
        get() = "allow"

    val NOT_ALLOW: String
        get() = "notallow"
}

inline fun FragmentManager.show(params: FragmentParams) {
    val existFrgmt = findFragmentByTag(params.fragment.name)
    if (existFrgmt != null && existFrgmt.isVisible) {
        // manager 내에 해당 fragment 가 이미 존재하면 해당 fragment 를 반환 한다
        return
    }

    val frgmt = params.fragment.newInstance() as Fragment
    val transaction = beginTransaction()

    params.bundle?.let { frgmt.arguments = it }

    transaction.run {
        params.anim?.let {
            when (it) {
                FragmentAnim.RIGHT -> setCustomAnimations(R.anim.slide_in_current, R.anim.slide_in_next,
                    R.anim.slide_out_current, R.anim.slide_out_prev)
                FragmentAnim.LEFT -> setCustomAnimations(R.anim.slide_out_current, R.anim.slide_out_prev,
                    R.anim.slide_in_current, R.anim.slide_in_next)
                FragmentAnim.UP -> setCustomAnimations(R.anim.slide_up_current, R.anim.slide_up_next,
                    R.anim.slide_down_current, R.anim.slide_down_prev)
                FragmentAnim.ALPHA -> setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                else -> { }
            }
        }

        if (params.add) {
            add(params.containerId, frgmt, frgmt.javaClass.name)
        } else {
            replace(params.containerId, frgmt, frgmt.javaClass.name)
        }

        if (params.backStack) {
            addToBackStack(frgmt.javaClass.name)
        }

        params.commit?.let {
            when (it) {
                FragmentCommit.ALLOW -> commitAllowingStateLoss()
                FragmentCommit.NOW -> commitNow()
                FragmentCommit.NOT_ALLOW -> commitNowAllowingStateLoss()
                else -> commit()
            }
        } ?: commit()
    }
}

inline fun FragmentManager.remove(clazz: Class<*>) {
    findFragmentByTag(clazz.name)?.let {
        beginTransaction().remove(it).commit()
    } ?: throw AssertionError("Fragment Not Found : ${clazz.name}")
}

inline fun FragmentManager.pop() {
    popBackStack()
}

inline fun FragmentManager.showDialog(frgmt: DialogFragment, name: String) {
    val transaction = beginTransaction()
    findFragmentByTag(name)?.let { transaction.remove(it) }

    frgmt.show(transaction, name)
}

inline fun FragmentManager.popAll() {
    (0..count - 1).map { popBackStack(it, FragmentManager.POP_BACK_STACK_INCLUSIVE) }
}

data class FragmentParams(
    @IdRes val containerId: Int,
    val fragment: Class<*>,
    val add: Boolean = true,
    var anim: String? = null,
    var bundle: Bundle? = null,
    var commit: String? = null,
    var backStack: Boolean = true
)

inline fun Fragment.generateLayoutName(): String {
    val name = javaClass.simpleName
    var layoutName = name.get(0).toLowerCase().toString()

    name.substring(1, name.length).forEach {
        layoutName += if (it.isUpperCase()) {
            "_${it.toLowerCase()}"
        } else {
            it
        }
    }

    return layoutName
}

inline fun Fragment.generateEmptyLayout(name: String): LinearLayout {
    val view = LinearLayout(activity)
    view.lpmm()
    view.gravity = Gravity.CENTER

    val text = TextView(activity)
    text.gravityCenter()
    text.text = "FILE NOT FOUND (${name})"
    view.addView(text)

    return view
}
