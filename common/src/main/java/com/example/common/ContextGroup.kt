@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 1. 24.. <p/>
 */

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

/**
 * pkgName 에 해당하는 앱이 foreground 인지 확인
 */
inline fun Context.isForegroundApp(pkgName: String) = systemService(ActivityManager::class.java)?.run {
    runningAppProcesses.filter {
        it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            && it.processName == pkgName
    }.size == 1
}

/**
 * 현재 앱이 foreground 인지 확인
 */
inline fun Context.isForegroundApp() = isForegroundApp(packageName)

inline fun Context.isInstalledPackage(packageName: String)= try {
    packageManager?.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}

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

/**
 * open keyboard
 */
inline fun Context.showKeyboard(view: View?) {
    view?.let {
        it.postDelayed({
            it.requestFocus()
            systemService(InputMethodManager::class.java)?.run {
                showSoftInput(it, InputMethodManager.SHOW_FORCED)
            }
        }, 400)
    }
}

/**
 * hide keyboard
 */
inline fun Context.hideKeyboard(view: View?) {
    view?.let {
        systemService(InputMethodManager::class.java)?.run {
            hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}

/**
 * force hide keyboard
 */
inline fun Context.forceHideKeyboard(window: Window?) {
    window?.run { setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) }
}

/**
 * 문자열 데이터를 얻는다.
 */
inline fun Context.string(@StringRes resid: Int) = getString(resid)

/**
 * 문자열 데이터를 얻는다.
 */
inline fun Context.string(resid: String) =
    string(stringId(resid))

inline fun Context.stringId(resid: String) =
    resources.getIdentifier(resid, "string", packageName)


////////////////////////////////////////////////////////////////////////////////////
//
// ENVIRONMENT EXTENSION
//
////////////////////////////////////////////////////////////////////////////////////

/** sdcard 경로 반환 */
inline fun Context.sdPath() = Environment.getExternalStorageDirectory().absolutePath

/**
 * clipboard 데이터를 읽어온다.
 */
inline fun Context.clipboard(): String? = systemService(ClipboardManager::class.java)?.run {
    primaryClip?.getItemAt(0)?.text.toString()
}

/**
 * clipboard 떼이터를 설정 한다.
 */
inline fun Context.clipboard(value: String) = systemService(ClipboardManager::class.java)?.run {
    primaryClip = ClipData.newPlainText("hspdata", value)
}


inline fun <T> Context.systemService(serviceClass: Class<T>): T? {
    return ContextCompat.getSystemService(this, serviceClass)
}

inline fun Context.availMem(percent: Int): Long {
    val mi = ActivityManager.MemoryInfo()
    systemService(ActivityManager::class.java)?.getMemoryInfo(mi)

    return percent.toLong() * mi.availMem / 100L
}