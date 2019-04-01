package com.example.clone_daum.ui.browser

import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.model.local.*
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserViewModel @Inject constructor(app: Application
    , var urlDao: UrlHistoryDao
    , val zzimDao: ZzimDao
) : CommandEventViewModel(app), ISnackbarAware, IWebViewEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserViewModel::class.java)

        const val CMD_BACK             = "back"
        const val CMD_SEARCH_FRAGMENT  = "search"
        const val CMD_SUBMENU_FRAGMENT = "submenu"
        const val CMD_SHARE_EVENT      = "share"
        const val CMD_HOME             = "home"
        const val CMD_GOTO_TOP         = "goto-top"
        const val CMD_NORMALSCREEN     = "normalscreen"
        const val CMD_RELOAD           = "reload"
    }

    override val snackbarEvent = SingleLiveEvent<String>()
    override val webviewEvent  = ObservableField<WebViewEvent>()

    private lateinit var mDisposable: CompositeDisposable

    val urlString           = ObservableField<String>()
    val brsCount            = ObservableField<String>()
    val sslIconResId        = ObservableInt(R.drawable.ic_vpn_key_black_24dp)
    val reloadIconResId     = ObservableInt(R.drawable.ic_clear_black_24dp)
    val valProgress         = ObservableInt()
    val visibleProgress     = ObservableInt(View.VISIBLE)
    val visibleSslIcon      = ObservableInt(View.GONE)
    val enableForward       = ObservableBoolean(false)
    val isFullscreen        = ObservableBoolean(false)

    val brsUrlBarAni    = ObservableField<AnimParams>()
    val brsAreaAni      = ObservableField<AnimParams>()
    val brsGoTop        = ObservableField<AnimParams>()

    fun init(disposable: CompositeDisposable) {
        mDisposable = disposable
    }

    fun applyUrl(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("APPLY URL : $url")
        }

        visibleSslIcon.set(if (url.contains("https://")) View.VISIBLE else View.GONE)

        urlString.set(url)
        urlDao.insert(UrlHistory(url = url, date = System.currentTimeMillis()))
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun applyBrsCount(count: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BRS COUNT : $count")
        }

        brsCount.set("$count")
    }

    fun eventReloadBrowser(url : String) {
        if (reloadIconResId.get() == R.drawable.ic_clear_black_24dp) {
            if (mLog.isDebugEnabled) {
                mLog.debug("STOP")
            }

            webviewEvent(WebViewEvent.STOP_LOADING)
        } else {
            if (mLog.isDebugEnabled) {
                mLog.debug("RELOAD BROWSER $url")
            }

            commandEvent(CMD_RELOAD)
        }
    }

    fun addZzim(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("ADD ZZIM $url")
        }

        if (url != urlString.get()) {
            mLog.error("ERROR: $url")
            mLog.error("ERROR: ${urlString.get()}")
        }

        mDisposable.add(zzimDao.hasUrl(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it > 0) {
                    if (mLog.isInfoEnabled) {
                        mLog.info("EXIST URL : $url ($it)")
                    }

                    snackbar(string(R.string.brs_exist_fav_url))
                } else {
                    insertZzim(url)
                }
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
                snackbar(it.message)
            }))
    }

    private fun insertZzim(url: String) {
        mDisposable.add(zzimDao.insert(Zzim(url = url
            , title = "title"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("ZZIM URL : $url")
                }

                snackbar(string(R.string.brs_fav_url_ok))
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
                snackbar(it.message)
            }))
    }
}