package com.example.clone_daum.ui.browser

import android.app.Application
import android.view.View
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.example.clone_daum.R
import com.example.clone_daum.model.local.*
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.viewmodel.CommandEventViewModel
import brigitte.widget.IWebViewEventAware
import brigitte.widget.WebViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserViewModel @Inject constructor(
    private var urlHistoryDao: UrlHistoryDao,
    private val zzimDao: ZzimDao,
    private val dp: CompositeDisposable,
    app: Application
) : CommandEventViewModel(app), IWebViewEventAware, ISeekBarProgressChanged {
    companion object {
        private val logger = LoggerFactory.getLogger(BrowserViewModel::class.java)

        const val CMD_BACK             = "back"
        const val CMD_SEARCH_FRAGMENT  = "search-fragment"
        const val CMD_SUBMENU_FRAGMENT = "submenu-fragment"
        const val CMD_SHARE_EVENT      = "share"
        const val CMD_HOME             = "home"
        const val CMD_GOTO_TOP         = "goto-top"
        const val CMD_NORMALSCREEN     = "normalscreen"
        const val CMD_RELOAD           = "reload"

        const val CMD_INNER_SEARCH_PREV = "inner-search-prev"
        const val CMD_INNER_SEARCH_NEXT = "inner-search-next"

        const val SPF_FONT_SIZE        = "spf-text-size"

        const val V_DEFAULT_TEXT_SIZE  = 50
    }

    override val webviewEvent = ObservableField<WebViewEvent>()

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

    val brsUrlBarAni        = ObservableField<AnimParams>()
    val brsAreaAni          = ObservableField<AnimParams>()
    val brsGoTop            = ObservableField<AnimParams>()
    val innerSearch         = ObservableField<String>()
    val innerSearchCount    = ObservableField<String>()

    // fontsize

    val brsFontSizeProgress = ObservableInt(app.prefs().getInt(SPF_FONT_SIZE, 50))
    val visibleBrsFontSize  = ObservableInt(View.GONE)
    val brsFontSizeText     = ObservableField<String>()
    val brsFontSizeLive     = MutableLiveData<Int>()

    init {
        applyWebViewFontSize()
    }

    fun applyUrl(url: String) {
        visibleSslIcon.set(if (url.contains("https://")) View.VISIBLE else View.GONE)
        urlString.set(url)
    }

    fun addHistory(url: String, title: String) {
        if (logger.isDebugEnabled) {
            logger.debug("ADD HISTORY : $title ($url)")
        }

        dp.add(urlHistoryDao.hasUrl(url)
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it == 0) {
                    urlHistoryDao.insert(UrlHistory(title, url, System.currentTimeMillis()))
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            if (logger.isDebugEnabled) {
                                logger.debug("ADDED URL HISTORY : $title ($url)")
                            }
                        }, { e -> errorLog(e, logger) })
                } else {
                    if (logger.isDebugEnabled) {
                        logger.debug("EXIST URL HISTORY : $title ($url)")
                    }
                }
            }, { e -> errorLog(e, logger) }))
    }

    fun applyBrowserCount(count: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("BRS COUNT : $count")
        }

        brsCount.set("$count")
    }

    fun eventReloadBrowser(url : String) {
        if (reloadIconResId.get() == R.drawable.ic_clear_black_24dp) {
            if (logger.isDebugEnabled) {
                logger.debug("STOP")
            }

            webviewEvent(WebViewEvent.STOP_LOADING)
        } else {
            if (logger.isDebugEnabled) {
                logger.debug("RELOAD BROWSER $url")
            }

            command(CMD_RELOAD)
        }
    }

    fun addZzim(url: String) {
        if (logger.isDebugEnabled) {
            logger.debug("ADD ZZIM $url")
        }

        if (url != urlString.get()) {
            logger.error("ERROR: $url")
            logger.error("ERROR: ${urlString.get()}")
        }

        dp.add(zzimDao.hasUrl(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == 0) {
                    insertZzim(url)
                } else {
                    if (logger.isInfoEnabled) {
                        logger.info("EXIST URL : $url ($it)")
                    }

                    snackbar(R.string.brs_exist_fav_url)
                }
            }, {
                errorLog(it, logger)
                snackbar(it)
            }))
    }

    private fun insertZzim(url: String) {
        dp.add(zzimDao.insert(Zzim(url = url
            , title = "title"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("ZZIM URL : $url")
                }

                snackbar(R.string.brs_fav_url_ok)
            }, {
                errorLog(it, logger)
                snackbar(it)
            }))
    }

    private fun applyWebViewFontSize() {
        brsFontSizeText.set("${app.prefs().getInt(SPF_FONT_SIZE, 50) + V_DEFAULT_TEXT_SIZE} %")
        brsFontSizeLive.value = brsFontSizeProgress.get()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISeekBarProgressChanged
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onProgressChanged(seekbar: SeekBar, value: Int, fromUser: Boolean) {
        if (logger.isTraceEnabled) {
            logger.trace("CHANGED FONT SIZE : $value")
        }

        // view 에는 realtime 으로 현재 화면을 갱신해야 하고
        brsFontSizeText.set("${value + V_DEFAULT_TEXT_SIZE} %")
    }

    override fun onStopTrackingTouch(seekbar: SeekBar) {
        // 실제 반영은 tracking 이 종료 된 후에 반영한다.
        val value = seekbar.progress
        if (logger.isDebugEnabled) {
            logger.debug("STOP TRACKING TOUCH : $value")
        }

        brsFontSizeLive.value = value
        app.prefs().edit(false) { putInt(SPF_FONT_SIZE, value) }
    }
}