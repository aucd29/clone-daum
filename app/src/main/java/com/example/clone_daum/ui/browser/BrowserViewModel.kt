package com.example.clone_daum.ui.browser

import android.app.Application
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.clone_daum.model.local.UrlHistory
import com.example.clone_daum.model.local.UrlHistoryDao
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserViewModel @Inject constructor(application: Application
        , val favDao: MyFavoriteDao
        , val disposable: CompositeDisposable)
    : AndroidViewModel(application)
    , ISnackbarAware, IFinishFragmentAware {

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
    val brsUrlBarAni    = ObservableField<AnimParams>()
    val brsAreaAni      = ObservableField<AnimParams>()

    val backEvent       = SingleLiveEvent<Void>()
    val forwardEvent    = SingleLiveEvent<Void>()
    val searchEvent     = SingleLiveEvent<Void>()
    val submenuEvent    = SingleLiveEvent<Void>()
    val reloadEvent     = SingleLiveEvent<String>()
    val shareEvent      = SingleLiveEvent<String>()

    override val snackbarEvent = SingleLiveEvent<String>()
    override val finishEvent   = SingleLiveEvent<Void>()

    fun applyUrl(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INSERT URL : $url")
        }

        visibleSslIcon.set(if (url.contains("https://")) View.VISIBLE else View.GONE)
        urlString.set(url)
        urlDao.insert(UrlHistory(url = url, date = System.currentTimeMillis()))
            .subscribeOn(Schedulers.io()).subscribe()
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

        finishEvent.call()
    }

    fun eventFavorite(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("FAVORITE EVENT $url")
        }

        disposable.add(favDao.isFavorite(url)
            .subscribeOn(Schedulers.io())
            .subscribe { cnt, e ->
                if (e != null) {
                    observeSnackbarEvent(e.message)
                } else {
                    if (cnt > 0) {
                        observeSnackbarEvent(string(R.string.brs_exist_fav_url))
                    } else {
                        disposable.add(favDao.insert(MyFavorite(url = url, date = time()))
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("FAV URL : $url")
                                }

                                observeSnackbarEvent(string(R.string.brs_fav_url_ok))
                            }, { observeSnackbarEvent(it.message) }))
                    }
                }
            })
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

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISnackbarAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun snackbarEvent(@StringRes resid: Int) =
        snackbarEvent(string(resid))

    override fun snackbarEvent(msg: String?) {
        mLog.error("ERROR: $msg")

        super.snackbarEvent(msg)
    }

    private inline fun observeSnackbarEvent(msg: String?) {
        disposable.add(Single.just(msg).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { msg, _ -> snackbarEvent(msg) })
    }
}