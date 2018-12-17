package com.example.clone_daum.ui.browser

import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import com.example.clone_daum.model.local.UrlHistory
import com.example.clone_daum.model.local.UrlHistoryDao
import com.example.common.WebViewSettingParams
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application) {

    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserViewModel::class.java)
    }

    @Inject lateinit var urlDao: UrlHistoryDao

    val urlString       = ObservableField<String>()
    val brsCount        = ObservableField<String>()
    val sslIconResId    = ObservableInt(R.drawable.ic_vpn_key_black_24dp)
    val valProgress     = ObservableInt()
    val visibleProgress = ObservableInt(View.VISIBLE)
    val visibleSslIcon  = ObservableInt(View.GONE)
    val enableForward   = ObservableBoolean(false)

    val brsSetting      = ObservableField<WebViewSettingParams>()
    val brsPauseTimer   = ObservableField<Boolean>()
    val brsUrlBarAni    = ObservableField<AnimParams>()
    val brsAreaAni      = ObservableField<AnimParams>()

    val backEvent       = SingleLiveEvent<Void>()
    val forwardEvent    = SingleLiveEvent<Void>()
    val homeEvent       = SingleLiveEvent<Void>()
    val searchEvent     = SingleLiveEvent<Void>()
    val submenuEvent    = SingleLiveEvent<Void>()

    val reloadEvent     = SingleLiveEvent<String>()
    val favoriteEvent   = SingleLiveEvent<String>()
    val shareEvent      = SingleLiveEvent<String>()

    fun applyUrl(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INSERT URL : $url")
        }

        visibleSslIcon.set(if (url.contains("https://")) View.VISIBLE else View.GONE)
        urlString.set(url)
        urlDao.insert(UrlHistory(url = url, date = System.currentTimeMillis()))
    }

    fun applyBrsCount(count: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("SET BRS COUNT : $count")
        }

        brsCount.set("$count")
    }

    fun eventBack() {
        if (mLog.isDebugEnabled) {
            mLog.debug("BACK PRESSED EVENT")
        }

        backEvent.call()
    }

    fun eventReloadBrowser(url : String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("RELOAD BROWSER $url")
        }

        reloadEvent.value = url
    }

    fun eventForward() {
        if (mLog.isDebugEnabled) {
            mLog.debug("FORWARD PRESSED EVENT")
        }

        forwardEvent.call()
    }

    fun eventHome() {
        if (mLog.isDebugEnabled) {
            mLog.debug("HOME EVENT")
        }

        homeEvent.call()
    }

    fun eventFavorite(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("FAVORITE EVENT $url")
        }

        favoriteEvent.value = url
    }

    fun eventSearchFragment() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SHOW SEARCH FRAGMENT")
        }

        searchEvent.call()
    }

    fun eventShareUrl(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("SHARE URL : $url")
        }

        shareEvent.value = url
    }

    fun eventSubMenu() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SUB MENU ")
        }

        submenuEvent.call()
    }

}