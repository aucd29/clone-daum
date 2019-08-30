@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import brigitte.arch.SingleLiveEvent
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 10. 30. <p/>
 *
 * Activity 관련 된 extension 모음
 */

// LifecycleObserver 으로 대체
//interface ILifeCycle {
//    fun onPause()
//    fun onResume()
//    fun onDestroy()
//}

//https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android
inline fun Activity.changeStatusBarColorRes(@ColorRes color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
}

inline fun Activity.changeStatusBarColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = color
    }
}

/**
 * snackbar 호출
 *
 * @param view 대상이 되는 뷰
 * @param msg 출력할 메시지
 * @param length 메시지 출력 시간 (기본값 LENGTH_SHORT)
 * @return Snackbar 객체
 */
inline fun Activity.snackbar(view: View, msg: String, length: Int = Snackbar.LENGTH_SHORT) =
    Snackbar.make(view, msg, length)

/**
 * snackbar 호출
 * @param view 대상이 되는 뷰
 * @param resid 출력할 메시지 리소스 아이디
 * @param length 메시지 출력 시간 (기본값 LENGTH_SHORT)
 * @return Snackbar 객체
 */
inline fun Activity.snackbar(view: View, @StringRes resid: Int, length: Int = Snackbar.LENGTH_SHORT) =
    snackbar(view, getString(resid), length)

inline fun Activity.errorLog(e: Throwable, logger: Logger) {
    if (logger.isDebugEnabled) {
        e.printStackTrace()
    }

    logger.error("ERROR: ${e.message}")
}

/**
 * 종료 하려면 다시 선택 하라는 메뉴 호출
 *
 * @param view snackbar 일 경우 대상이 되는 view
 * @return BackPressedManager 객체
 */
inline fun AppCompatActivity.checkBackPressed(view: View?) = BackPressedManager(this, view)

/**
 * 리소스 내 dimen 을 얻기 위한 shortcut
 *
 * @param resid dim 값에 해당하는 리소스 아이디
 * @return 아이디에 해당하는 값
 */
inline fun Activity.dimen(@DimenRes resid: Int) =
    resources.getDimension(resid)

/**
 * 다이얼로그를 띄우기 위한 옵저버 로 view model 에 선언되어 있는 single live event 의 값의 변화를 인지 하여 dialog 을 띄운다.
 *
 * @param event DialogParam 을 담는 LiveEvent
 * @param disposable CompositeDisposable 객체 (기본 값은 null)
 */
inline fun FragmentActivity.observeDialog(event: SingleLiveEvent<DialogParam>, disposable: CompositeDisposable? = null) {
    event.observe(this, Observer { dialog(it, disposable) })
}

/**
 * activity 의 클래스 명을 기반하여 inflate 할 layout 이름을 얻도록 한다. 이때 첫글자 뒤에 오는 대문자는 _ 을 붙이고 대문자는 소문자로 변환 된다.
 * 예로 TestActivity 라면 test_activity 형태로 변환 된다.
 */
inline fun Activity.generateLayoutName(): String {
    val name = javaClass.simpleName
    var layoutName = name[0].toLowerCase().toString()

    name.substring(1, name.length).forEach {
        layoutName += if (it.isUpperCase()) {
            "_${it.toLowerCase()}"
        } else {
            it
        }
    }

    return layoutName
}

/**
 * 화면을 ON / OFF 시킨다.
 *
 * @param on true일 경우 화면을 켜고 아닐경우 끈다.
 */
inline fun Activity.keepScreen(on: Boolean) {
    if (on) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

/**
 * 전체 화면으로 토글 한다.
 *
 * @param fullscreen true 일 경우 전체화면 아니면 일반화면
 */
inline fun Activity.fullscreen(fullscreen: Boolean) {
    runOnUiThread {
        val lp = window.attributes
        lp.let {
            if (fullscreen) {
                it.flags = it.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            } else {
                it.flags = it.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            }

            window.attributes = it
        }
    }
}

/**
 * 전체화면인지 아닌지 반환 한다.
 * @return true 면 전체 화면 false 면 일반화면
 */
inline fun Activity.isFullscreen() =
    (window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN

//inline fun AppCompatActivity.backPressedCallback(crossinline f: () -> Boolean) = with(onBackPressedDispatcher) {
//    addCallback(this@backPressedCallback, object: OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            isEnabled = f()
//        }
//    })
//}

inline fun Activity.exceptionCatcher(noinline callback: (String) -> Unit) {
    // setting exception
    val handler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        val os = ByteArrayOutputStream()
        val s  = PrintStream(os)
        e.printStackTrace(s)
        s.flush()

        callback.invoke("ERROR: $os")

        if (handler != null) {
            handler.uncaughtException(t, e)
        } else {
            callback.invoke("ERROR: EXCEPTION HANDLER == null")

            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}

fun Activity.chromeInspector(log: ((String) -> Unit)? = null) {
    if (BuildConfig.DEBUG) {
        // enabled chrome inspector
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            log?.invoke("ENABLED CHROME INSPECTOR")
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// Dialog
//
////////////////////////////////////////////////////////////////////////////////////

/**
 * 다이얼로그를 띄우기 위한 속성을 관리하는 데이터 클래스
 */
data class DialogParam constructor (
    var message: String? = null,        // 다이얼로그에 출력할 메시지
    var title: String? = null,          // 다이얼로그 타이틀
    var positiveStr: String? = null,    // positive 버튼에 출력할 문구
    var negativeStr: String? = null,    // negative 버튼에 출력할 문구
    var listener: ((Boolean, DialogInterface) -> Unit)? = null, // 사용자가 positive 또는 negative 를 선택할 결과를 전달 받는 리스너
    var timer: Int = 0,                 // 다이얼로그를 시간에 따라 자동으로 종료 시킬 타이머 값
    val context: Context? = null,       // application context
    var messageId: Int? = null,         // 출력할 메시지에 해당하는 리소스 아이디
    var titleId: Int? = null,           // 출력할 타이틀에 해당하는 리소스 아이디
    var positiveId: Int? = null,        // 출력할 positive 버튼에 해당하는 리소스 아이디
    var negativeId: Int? = null,        // 출력할 negative 버튼에 해당하는 리소스 아이디
    var view: View? = null,
    var dialog: Dialog? = null
) {
    init {
        context?.apply {
            messageId?.apply  { message     = string(this) }
            titleId?.apply    { title       = string(this) }
            negativeId?.apply { negativeStr = string(this) }

            positiveStr = string(if (positiveId != null) positiveId!! else android.R.string.ok)
        }
    }
}

/**
 * 다이얼로그를 띄운다.
 *
 * @param params 다이얼로그를 열기 위한 속성 값
 * @param disposable rx 의 interval 을 통해 다이얼로그르 자동 종료 시키는데 이때 필요한 CompositeDisposable 값
 */
inline fun Activity.dialog(params: DialogParam, disposable: CompositeDisposable? = null) {
    val bd = AlertDialog.Builder(this)

    if (params.view != null) {
        bd.setView(params.view)
        params.dialog = bd.show()
    } else {
        bd.setMessage(params.message)
        params.apply {
            title?.let { bd.setTitle(it) }
            positiveStr?.let { bd.setPositiveButton(it) { dlg, _ ->
                dlg.dismiss()
                listener?.invoke(true, dlg)
            }}
            negativeStr?.let { bd.setNegativeButton(it) { dlg, _ ->
                dlg.dismiss()
                listener?.invoke(false, dlg)
            }}

            val dlg = bd.show()
            if (timer > 0) {
                disposable?.add(Observable.interval(1, TimeUnit.SECONDS)
                    .take(1).subscribe { dlg.dismiss() })
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BackPressedManager
//
////////////////////////////////////////////////////////////////////////////////////

/**
 * 앱 종료를 위해 다시 한번 backkey 를 선택하라는 문구를 동작 시키기 위한 클래스
 */
open class BackPressedManager constructor (
    private var mActivity: AppCompatActivity, var view: View? = null
) {
    companion object {
        const val DELAY = 2000
    }

    protected var mToast: Toast? = null
    protected var mSnackbar: Snackbar? = null
    protected var mPressedTime: Long = 0

    private fun time() = mPressedTime + DELAY

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
