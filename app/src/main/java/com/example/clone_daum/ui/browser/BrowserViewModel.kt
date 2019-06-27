package com.example.clone_daum.ui.browser

import android.app.Application
import android.view.View
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.model.local.*
import brigitte.*
import brigitte.bindingadapter.AnimParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserViewModel @Inject constructor(app: Application
    , private var mUrlHistoryDao: UrlHistoryDao
    , private val mZzimDao: ZzimDao
) : CommandEventViewModel(app), IWebViewEventAware, ISeekBarProgressChanged {
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

        const val CMD_INNER_SEARCH_PREV = "inner-search-prev"
        const val CMD_INNER_SEARCH_NEXT = "inner-search-next"

        const val SPF_FONT_SIZE        = "spf-text-size"
    }

    override val webviewEvent = ObservableField<WebViewEvent>()
    private lateinit var mDisposable: CompositeDisposable

    val urlString           = ObservableField<String>()
    val brsCount            = ObservableField<String>()
    val sslIconResId        = ObservableInt(R.drawable.ic_vpn_key_black_24dp)
    val reloadIconResId     = ObservableInt(R.drawable.ic_clear_black_24dp)
    val valProgress         = ObservableInt()
    val visibleProgress     = ObservableInt(View.VISIBLE)
    val visibleSslIcon      = ObservableInt(View.GONE)
    val visibleInnerSearch  = ObservableInt(View.GONE)
    val enableForward       = ObservableBoolean(false)
    val isFullscreen        = ObservableBoolean(false)

    val brsUrlBarAni     = ObservableField<AnimParams>()
    val brsAreaAni       = ObservableField<AnimParams>()
    val brsGoTop         = ObservableField<AnimParams>()
    val innerSearch      = ObservableField<String>()
    val innerSearchCount = ObservableField<String>()

    // fontsize

    val brsFontSizeProgress = ObservableInt(prefs().getInt(SPF_FONT_SIZE, 50))
    val visibleBrsFontSize  = ObservableInt(View.GONE)


    fun init(disposable: CompositeDisposable) {
        mDisposable = disposable
    }

    fun applyUrl(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("APPLY URL : $url")
        }

        visibleSslIcon.set(if (url.contains("https://")) View.VISIBLE else View.GONE)
        urlString.set(url)
    }

    fun addHistory(url: String, title: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("ADD HISTORY : $title ($url)")
        }

        mDisposable.add(mUrlHistoryDao.hasUrl(url)
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it == 0) {
                    mUrlHistoryDao.insert(UrlHistory(title, url, System.currentTimeMillis()))
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            if (mLog.isDebugEnabled) {
                                mLog.debug("ADDED URL HISTORY : $title ($url)")
                            }
                        }, { e -> errorLog(e, mLog) })
                } else {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("EXIST URL HISTORY : $title ($url)")
                    }
                }
            }, { e -> errorLog(e, mLog) }))
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

            command(CMD_RELOAD)
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

        mDisposable.add(mZzimDao.hasUrl(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == 0) {
                    insertZzim(url)
                } else {
                    if (mLog.isInfoEnabled) {
                        mLog.info("EXIST URL : $url ($it)")
                    }

                    snackbar(R.string.brs_exist_fav_url)
                }
            }, {
                errorLog(it, mLog)
                snackbar(it)
            }))
    }

    private fun insertZzim(url: String) {
        mDisposable.add(mZzimDao.insert(Zzim(url = url
            , title = "title"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("ZZIM URL : $url")
                }

                snackbar(R.string.brs_fav_url_ok)
            }, {
                errorLog(it, mLog)
                snackbar(it)
            }))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISeekBarProgressChanged
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onProgressChanged(seekbar: SeekBar, value: Int, fromUser: Boolean) {
        if (mLog.isDebugEnabled) {
            mLog.debug("CHANGED FONT SIZE : $value")
        }

        brsFontSizeProgress.set(value)
        prefs().edit { putInt(SPF_FONT_SIZE, value) }
    }
}