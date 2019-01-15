package com.example.clone_daum.ui.main

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.R
import com.example.clone_daum.di.module.Config
import com.example.clone_daum.di.module.PreloadConfig
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(val app: Application
    , val config: Config
    , val preConfig: PreloadConfig
    , val disposable: CompositeDisposable
) : AndroidViewModel(app), ICommandEventAware {

    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1

        const val CMD_SEARCH_FRAMGNET         = "search"
        const val CMD_NAVIGATION_FRAGMENT     = "navigation"
        const val CMD_REALTIME_ISSUE_FRAGMENT = "realtime-issue"
        const val CMD_WEATHER_FRAGMENT        = "weahter"
        const val CMD_BRS_OPEN                = "brs-open"
        const val CMD_PERMISSION_GPS          = "permission-gps"
    }

    override val commandEvent    = SingleLiveEvent<Pair<String, Any?>>()

    val tabAdapter               = ObservableField<MainTabAdapter>()
    val viewpager                = ObservableField<ViewPager>()
    val brsSetting               = ObservableField<WebViewSettingParams>()
    val brsEvent                 = ObservableField<WebViewEvent>()
    val viewpagerPageLimit       = ObservableInt(3)
    val visibleBack              = ObservableInt(View.GONE)
    val visibleGps               = ObservableInt(if (config.HAS_PERMISSION_GPS) View.GONE else View.VISIBLE)
    var gotoNewsEvent            = ObservableInt(0)

    // viewpager 에 adapter 가 set 된 이후 시점을 알려줌 (ViewPagerBindingAdapter)
    val appbarOffsetChangedEvent = ObservableField<(AppBarLayout, Int) -> Unit>()
    val appbarOffsetLiveEvent    = MutableLiveData<Int>()

    val realtimeIssueText        = ObservableField<String>()
    var realtimeCount            = 0


    fun gotoNews() {
        if (mLog.isDebugEnabled) {
            mLog.debug("GOTO TAB $INDEX_NEWS")
        }

        if (gotoNewsEvent.get() == INDEX_NEWS) {
            gotoNewsEvent.notifyChange()
        } else {
            gotoNewsEvent.set(INDEX_NEWS)
        }
    }

    fun searchExtendMenu() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // REALTIME ISSUE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun startRealtimeIssue() {
        if (preConfig.realtimeIssueList.size == 0) {
            realtimeIssueText.set(string(R.string.main_realtime_issue_network_error))
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("START REALTIME ISSUE")
        }

        preConfig.realtimeIssueList.get(0).second.let { issueList ->
            val index = realtimeCount % issueList.size
            val issue = issueList.get(index)

            realtimeIssueText.set("${index + 1} ${issue.text}")

            disposable.add(Observable.interval(7, TimeUnit.SECONDS).repeat().subscribe {
                val index = realtimeCount % issueList.size
                val issue = issueList.get(index)

                realtimeIssueText.set("${index + 1} ${issue.text}")
                ++realtimeCount

                if (mLog.isTraceEnabled) {
                    mLog.trace("TIMER EXPLODE $realtimeCount ${issue.text} ")
                }
            })
        }
    }

    fun stopRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("STOP REALTIME ISSUE")
        }

        disposable.clear()
    }

    fun realtimeIssueOpenEvent(text: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("realtimeIssueOpenEvent : $text")
        }

        val newText = text.replace("[0-9]".toRegex(), "").trim()
        preConfig.realtimeIssueList.get(0).second.let { list ->
            var i = 0
            while (i < list.size) {
                val it = list.get(i)

                if (it.text == newText) {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("REALTIME ISSUE OPEN : ${it.text} : ${it.url}")
                    }

                    commandEvent(CMD_BRS_OPEN, it.url)
                    break
                }

                ++i
            }
        }
    }

    fun webviewBack() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

}
