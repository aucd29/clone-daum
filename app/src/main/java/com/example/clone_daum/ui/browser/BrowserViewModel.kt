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

class BrowserViewModel @Inject constructor(app: Application
   , var urlDao: UrlHistoryDao
   , val favDao: MyFavoriteDao
   , val disposable: CompositeDisposable
) : AndroidViewModel(app), ISnackbarAware, IFinishFragmentAware, ICommandEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserViewModel::class.java)

        const val CMD_BACK             = "back"
        const val CMD_SEARCH_FRAGMENT  = "search"
        const val CMD_SUBMENU_FRAGMENT = "submenu"
        const val CMD_SHARE_EVENT      = "share"
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override val commandEvent  = SingleLiveEvent<Pair<String, Any?>>()
    override val snackbarEvent = SingleLiveEvent<String>()
    override val finishEvent   = SingleLiveEvent<Void>()

    val urlString       = ObservableField<String>()
    val brsCount        = ObservableField<String>()
    val sslIconResId    = ObservableInt(R.drawable.ic_vpn_key_black_24dp)
    val reloadIconResId = ObservableInt(R.drawable.ic_clear_black_24dp)
    val valProgress     = ObservableInt()
    val visibleProgress = ObservableInt(View.VISIBLE)
    val visibleSslIcon  = ObservableInt(View.GONE)
    val enableForward   = ObservableBoolean(false)

    val brsSetting      = ObservableField<WebViewSettingParams>()
    val brsEvent        = ObservableField<WebViewEvent>()
    val brsUrlBarAni    = ObservableField<AnimParams>()
    val brsAreaAni      = ObservableField<AnimParams>()

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
            mLog.debug("SET BRS COUNT : $count")
        }

        brsCount.set("$count")
    }

    fun eventReloadBrowser(url : String) {
        if (reloadIconResId.get() == R.drawable.ic_clear_black_24dp) {
            if (mLog.isDebugEnabled) {
                mLog.debug("STOP")
            }

            brsEvent.set(WebViewEvent.STOP_LOADING)
        } else {
            if (mLog.isDebugEnabled) {
                mLog.debug("RELOAD BROWSER $url")
            }

            brsEvent.set(WebViewEvent.RELOAD)
        }
    }

    fun eventForward() {
        if (mLog.isDebugEnabled) {
            mLog.debug("FORWARD PRESSED EVENT")
        }

        brsEvent.set(WebViewEvent.FORWARD)
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

        if (url != urlString.get()) {
            mLog.error("ERROR: $url")
            mLog.error("ERROR: ${urlString.get()}")
        }

        disposable.add(favDao.hasUrl(url)
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it > 0) {
                    if (mLog.isInfoEnabled) {
                        mLog.info("EXIST URL : $url ($it)")
                    }

                    observeSnackbarEvent(string(R.string.brs_exist_fav_url))
                } else {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("FAV URL : $url ($it)")
                    }

                    insertFavUrl(url)
                }
            }, { e ->
                if (mLog.isDebugEnabled) {
                    e.printStackTrace()
                }

                mLog.error("ERROR: ${e.message}")
                observeSnackbarEvent(e.message)
            })
        )
    }

    private fun insertFavUrl(url: String) {
        disposable.add(favDao.insert(MyFavorite(url = url, date = time()))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAV URL : $url")
                }

                observeSnackbarEvent(string(R.string.brs_fav_url_ok))
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
                observeSnackbarEvent(it.message)
            }))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISnackbarAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun snackbar(@StringRes resid: Int) =
        snackbar(string(resid))

    private inline fun observeSnackbarEvent(msg: String?) {
        disposable.add(Single.just(msg).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { msg, _ -> snackbar(msg) })
    }
}