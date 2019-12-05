@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.io.File

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 1. 24.. <p/>
 */

inline fun Context.actionBarSize(): Float {
    val ta = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val size = ta.getDimension(0, 0f)
    ta.recycle()

    return size
}

inline fun Context.color(@ColorRes resid: Int) = ContextCompat.getColor(this, resid)

/**
 * pkgName 에 해당하는 앱이 foreground 인지 확인
 */
inline fun Context.isForegroundApp(pkgName: String) = systemService<ActivityManager>()?.apply {
    runningAppProcesses.filter {
        it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            && it.processName == pkgName
    }.size == 1
}

/**
 * 현재 앱이 foreground 인지 확인
 */
inline fun Context.isForegroundApp() = isForegroundApp(packageName)

/**
 * install 된 앱인지 파악
 */
inline fun Context.isInstalledPackage(packageName: String)= try {
    packageManager?.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}

/**
 * 앱이 설치되어 있으면 해당 앱을 구동 시키고 없으면 마켓 앱을 띄우도록 함
 */
inline fun Context.launchApp(packageName: String) {
    val intent = if (isInstalledPackage(packageName)) {
        packageManager.getLaunchIntentForPackage(packageName)
    } else {
        try {
            Intent(Intent.ACTION_VIEW, Uri.parse(
                "market://details?id=$packageName"))
        } catch (e: ActivityNotFoundException) {
            Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://play.google.com/store/apps/details?id=$packageName"))
        }
    }

    startActivity(intent)
}


/**
 * sdcard 내 app 경로 전달
 */
inline fun Context.externalFilePath() = getExternalFilesDir(null)?.absolutePath

/**
 * display density 반환
 */
inline fun Context.displayDensity() = resources.displayMetrics.density


// https://stackoverflow.com/questions/34631818/leakcanary-report-leak-in-inputmethodmanager

/**
 * open keyboard
 */
inline fun Context.showKeyboard(view: View?, flags: Int = InputMethodManager.SHOW_IMPLICIT) {
    view?.let {
        it.postDelayed({
            it.requestFocus()
            systemService<InputMethodManager>()?.showSoftInput(it, flags) // InputMethodManager.SHOW_FORCED
        }, 200)
    }
}

/**
 * hide keyboard
 */
inline fun Context.hideKeyboard(view: View?) {
    view?.let {
        systemService<InputMethodManager>()?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

/**
 * force hide keyboard
 */
inline fun Context.forceHideKeyboard(window: Window?) {
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

/**
 * 문자열 데이터를 얻는다.
 */
inline fun Context.string(@StringRes resid: Int): String = getString(resid)
inline fun Context.stringArray(@ArrayRes resid: Int): Array<String> = resources.getStringArray(resid)
inline fun Context.intArray(@ArrayRes resid: Int): IntArray = resources.getIntArray(resid)
inline fun Context.drawable(@DrawableRes resid: Int) = ContextCompat.getDrawable(this, resid)

/**
 * 문자열 데이터를 얻는다.
 */
inline fun Context.string(resid: String) =
    string(stringId(resid))

inline fun Context.stringId(resid: String) =
    resources.getIdentifier(resid, "string", packageName)

inline fun Context.drawable(strid: String) =
    drawable(drawableId(strid))

inline fun Context.drawableId(strid: String) =
    resources.getIdentifier(strid, "drawable", packageName)


////////////////////////////////////////////////////////////////////////////////////
//
// ENVIRONMENT EXTENSION
//
////////////////////////////////////////////////////////////////////////////////////

/** sdcard 경로 반환 */
inline fun Context.sdPath(): String = Environment.getExternalStorageDirectory().absolutePath

inline fun Context.dcim(): File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

/**
 * clipboard 데이터를 읽어온다.
 */
inline fun Context.clipboard(): String? =
    systemService<ClipboardManager>()?.primaryClip?.getItemAt(0)?.text.toString()

/**
 * clipboard 떼이터를 설정 한다.
 */
inline fun Context.clipboard(value: String) = systemService<ClipboardManager>()?.run {
    primaryClip = ClipData.newPlainText("burke-data", value)
}

/**
 * systemService 반환
 */
inline fun <reified T> Context.systemService(): T? {
    return ContextCompat.getSystemService(this, T::class.java)
}

/**
 * 유효 메모리 확인
 */
inline fun Context.availMem(percent: Int): Long {
    val mi = ActivityManager.MemoryInfo()
    systemService<ActivityManager>()?.getMemoryInfo(mi)

    return percent.toLong() * mi.availMem / 100L
}

/**
 * toast 띄우기
 */
inline fun Context.toast(message: String, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, len).show()
}

/**
 * toast 띄우기
 */
inline fun Context.toast(@StringRes resid: Int, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resid, len).show()
}
