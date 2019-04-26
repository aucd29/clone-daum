@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.app.Activity
import android.os.Build
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.fragment.app.Fragment

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-04-22 <p/>
 */

inline fun Activity.initCookieManager() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
        CookieSyncManager.createInstance(this)
    }

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
        CookieManager.getInstance().removeAllCookies(null)
    } else {
        CookieManager.getInstance().removeAllCookie()
    }
}

inline fun Activity.startCookieSync() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
        CookieSyncManager.getInstance().startSync()
    }
}

inline fun Activity.stopCookieSync() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
        CookieSyncManager.getInstance().stopSync()
    }
}

inline fun Activity.syncCookie() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        CookieManager.getInstance().flush()
    } else {
        CookieSyncManager.getInstance().sync()
    }
}

inline fun Fragment.syncCookie() {
    activity?.syncCookie()
}