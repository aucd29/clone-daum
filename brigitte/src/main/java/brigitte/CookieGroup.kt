@file:Suppress("NOTHING_TO_INLINE", "unused", "DEPRECATION")
package brigitte

import android.app.Activity
import android.os.Build
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.fragment.app.Fragment
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.UnsupportedOperationException
import java.net.CookiePolicy
import java.net.CookieStore
import java.net.URI
import java.util.*

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

class CookieManagerProxy(
    store: CookieStore? = null,
    policy: CookiePolicy = CookiePolicy.ACCEPT_ALL
): java.net.CookieManager(store, policy), CookieJar {
    private val mCookieManager = CookieManager.getInstance()

    override fun put(uri: URI?, responseHeaders: MutableMap<String, MutableList<String>>?) {
        if (uri == null || responseHeaders == null) {
            return
        }

        val url = uri.toString()
        val it = responseHeaders.iterator()
        while (it.hasNext()) {
            val item = it.next()
            val key = item.key

            if (!(SET_COOKIE.equals(key, true) || SET_COOKIE2.equals(key, true))) {
                continue
            }

            responseHeaders.get(key)?.forEach {
                mCookieManager.setCookie(uri.toString(), it)

                if (mLog.isDebugEnabled) {
                    mLog.debug("SET COOKIE: $it ($url)")
                }
            }
        }

        cookieSync()
    }

    private fun cookieSync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush()
        } else {
            // sync 가 내부적으로는 CookieManager.getInstance().flush() 를 호출 한다.
            CookieSyncManager.getInstance().sync()
        }
    }

    override fun get(
        uri: URI?,
        requestHeaders: MutableMap<String, MutableList<String>>?
    ): MutableMap<String, MutableList<String>> {
        if (uri == null || requestHeaders == null) {
            throw IllegalArgumentException("Arguments is null")
        }

        val url = uri.toString()
        val result = mutableMapOf<String, MutableList<String>>()
        val cookie = mCookieManager.getCookie(url)
        cookie?.let { result.put(COOKIE, Collections.singletonList(cookie)) }

        return result
    }

    override fun getCookieStore(): CookieStore {
        throw UnsupportedOperationException()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        val responseHeaders = mutableMapOf<String, MutableList<String>>()
        val cookieList = arrayListOf<String>()

        cookies.forEach { cookieList.add(it.toString()) }
        responseHeaders.put(SET_COOKIE, cookieList)

        try {
            put(url.uri(), responseHeaders)
        } catch (e: IOException) {
            if (mLog.isDebugEnabled) {
                e.printStackTrace()
            }

            mLog.error("ERROR: ${e.message}")
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val responseCookieList = mutableListOf<Cookie>()

        try {
            val mapCookie = get(url.uri(), mutableMapOf())
            mapCookie.forEach {
                it.value.forEach {
                    val cookies = it.split(";")

                    cookies.forEach {
                        Cookie.parse(url, it)?.let { responseCookieList.add(it) }
                    }
                }
            }
        } catch (e: IOException) {
            if (mLog.isDebugEnabled) {
                e.printStackTrace()
            }

            mLog.error("ERROR: ${e.message}")
        }

        return responseCookieList
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(CookieManagerProxy::class.java)

        private const val SET_COOKIE  = "Set-Cookie"
        private const val SET_COOKIE2 = "Set-Cookie2"
        private const val COOKIE      = "Cookie"
    }
}