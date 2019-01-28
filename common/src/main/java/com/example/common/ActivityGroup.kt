@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import com.example.common.arch.SingleLiveEvent
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 10. 30. <p/>
 */


/**
 * snackbar 호출
 */
inline fun Activity.snackbar(view: View, msg: String, length: Int = Snackbar.LENGTH_SHORT) =
    Snackbar.make(view, msg, length)

/**
 * snackbar 호출
 */
inline fun Activity.snackbar(view: View, @StringRes resid: Int, length: Int = Snackbar.LENGTH_SHORT) =
    snackbar(view, getString(resid), length)

inline fun AppCompatActivity.checkBackPressed(view: View?) = BackPressedManager(this, view)

inline fun Activity.dimen(@DimenRes resid: Int) =
    resources.getDimension(resid)

inline fun FragmentActivity.observeDialog(event: SingleLiveEvent<DialogParam>, disposable: CompositeDisposable? = null) {
    event.observe(this, Observer { dialog(it, disposable) })
}

inline fun Activity.generateLayoutName(): String {
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

inline fun Activity.keepScreen(on: Boolean) {
    if (on) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// Dialog
//
////////////////////////////////////////////////////////////////////////////////////

data class DialogParam (
    var message: String? = null,
    var title: String? = null,
    var positiveStr: String? = null,
    var negativeStr: String? = null,
    var listener: ((Boolean, DialogInterface) -> Unit)? = null,
    var timer: Int = 0,
    val context: Context? = null,
    var messageId: Int? = null,
    var titleId: Int? = null,
    var positiveId: Int? = null,
    var negativeId: Int? = null
) {
    init {
        context?.run {
            messageId?.run   { message     = string(this) }
            titleId?.run     { title       = string(this) }
            negativeStr?.run { negativeStr = string(this) }

            positiveStr = string(if (positiveId != null) positiveId!! else android.R.string.ok)
        }
    }
}

inline fun Activity.dialog(params: DialogParam, disposable: CompositeDisposable? = null) {
    val bd = AlertDialog.Builder(this)
    bd.setMessage(params.message)

    params.title?.run { bd.setTitle(this) }
    params.positiveStr?.run { bd.setPositiveButton(this) { dlg, _ ->
        params.listener?.invoke(true, dlg)
    }}

    params.negativeStr?.run { bd.setNegativeButton(this) { dlg, _ ->
        params.listener?.invoke(false, dlg)
    }}

    val dlg = bd.show()
    if (params.timer > 0) {
        disposable?.add(Observable.interval(1, TimeUnit.SECONDS)
            .take(1).subscribe { dlg.dismiss() })
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BackPressedManager
//
////////////////////////////////////////////////////////////////////////////////////

open class BackPressedManager(var mActivity: AppCompatActivity, var view: View? = null) {
    companion object {
        val delay = 2000
    }

    protected var mToast: Toast? = null
    protected var mSnackbar: Snackbar? = null
    protected var mPressedTime: Long = 0

    inline fun time() = mPressedTime + delay

    fun onBackPressed(): Boolean {
        if (mActivity.supportFragmentManager.backStackEntryCount > 0) {
            return true
        }

        if (System.currentTimeMillis() > time()) {
            mPressedTime = System.currentTimeMillis()
            show()

            return false
        }

        if (System.currentTimeMillis() <= time()) {
            view?.let { mSnackbar?.dismiss() } ?: mToast?.cancel()
            ActivityCompat.finishAffinity(mActivity)
        }

        return false
    }

    fun show() {
        if (view != null) {
            mSnackbar = mActivity.snackbar(view!!, R.string.activity_backkey_exit)
            mSnackbar?.show()
        }
    }
}
