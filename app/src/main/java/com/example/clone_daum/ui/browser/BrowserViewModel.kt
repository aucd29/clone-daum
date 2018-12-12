package com.example.clone_daum.ui.browser

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import com.example.clone_daum.model.local.UrlHistory
import com.example.clone_daum.model.local.UrlHistoryDao
import com.example.common.arch.SingleLiveEvent
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

    val visibleSslIcon = ObservableInt(View.GONE)

    val backEvent   = SingleLiveEvent<Void>()
    val reloadEvent = SingleLiveEvent<String>()

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
}