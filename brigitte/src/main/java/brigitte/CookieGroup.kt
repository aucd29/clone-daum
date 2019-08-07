@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.app.Activity
import android.os.Build
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.fragment.app.Fragment

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-04-22 <p/>
 */

inline fun Activity.initCookieManager() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        CookieSyncManager.createInstance(this)
    }

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
        CookieManager.getInstance().removeAllCookies(null)
    } else {
        CookieManager.getInstance().removeAllCookie()
    }
}

inline fun Activity.startCookieSync() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        CookieSyncManager.getInstance().startSync()
    }
}

inline fun Activity.stopCookieSync() {
    // 간만에 다시 보는데 버전 분기에 오류가 있어서 수정 ; ; ; [aucd29][2019-07-18]
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        CookieSyncManager.getInstance().stopSync()
    }
}

inline fun Activity.syncCookie() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        CookieManager.getInstance().flush()
    } else {
        // sync 가 내부적으로는 CookieManager.getInstance().flush() 를 호출 한다.
        CookieSyncManager.getInstance().sync()
    }
}

inline fun Fragment.syncCookie() {
    activity?.syncCookie()
}