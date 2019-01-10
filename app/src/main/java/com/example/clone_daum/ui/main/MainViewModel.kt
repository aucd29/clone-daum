package com.example.clone_daum.ui.main

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.R
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
    , val preConfig: PreloadConfig
    , val disposable: CompositeDisposable
) : AndroidViewModel(app), ICommandEventAware, IPairEventAware {

    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1

        const val CMD_SEARCH_FRAMGNET         = "search"
        const val CMD_NAVIGATION_FRAGMENT     = "navigation"
        const val CMD_REALTIME_ISSUE_FRAGMENT = "realtime-issue"

        const val PAIR_BRS_OPEN               = "brs-open"

        const val K_ALL_ISSUE                 = "전체 이슈검색어"
    }

    override val commandEvent   = SingleLiveEvent<String>()
    override val pairEvent      = SingleLiveEvent<Pair<String, Any>>()

    val tabAdapter              = ObservableField<MainTabAdapter>()
    val viewpager               = ObservableField<ViewPager>()
    val brsSetting              = ObservableField<WebViewSettingParams>()
    val brsEvent                = ObservableField<WebViewEvent>()
    val viewpagerPageLimit      = ObservableInt(3)
    val visibleBack             = ObservableInt(View.GONE)
    var gotoNewsEvent           = ObservableInt(0)

    // viewpager 에 adapter 가 set 된 이후 시점을 알려줌 (ViewPagerBindingAdapter)
//    val viewpagerLoadedEvent     = ObservableField<() -> Unit>()
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

    fun searchFragment() {
        commandEvent.value = CMD_SEARCH_FRAMGNET
    }

    fun searchExtendMenu() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun weatherDetail() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun confirmGps() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun openNavigation() {
        commandEvent.value = CMD_NAVIGATION_FRAGMENT
    }

    fun webviewBack() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun startRealtimeIssue() {
        if (preConfig.realtimeIssueMap.size == 0) {
            realtimeIssueText.set(string(R.string.main_realtime_issue_network_error))
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("START REALTIME ISSUE")
        }

        preConfig.realtimeIssueMap.get(K_ALL_ISSUE)?.let { issueList ->
            val index = realtimeCount % issueList.size
            val issue = issueList.get(index)

            realtimeIssueText.set("${index + 1} ${issue.text}")

            disposable.add(Observable.interval(5, TimeUnit.SECONDS).repeat().subscribe {
                val index = realtimeCount % issueList.size
                val issue = issueList.get(index)

                realtimeIssueText.set("${index + 1} ${issue.text}")

                ++realtimeCount
                if (mLog.isDebugEnabled) {
                    mLog.debug("TIMER EXPLODE $realtimeCount ${issue.text} ")
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
        preConfig.realtimeIssueMap.get(K_ALL_ISSUE)?.let { list ->
            var i = 0
            while (i < list.size) {
                val it = list.get(i)

                if (it.text == newText) {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("REALTIME ISSUE OPEN : ${it.text} : ${it.url}")
                    }

                    pairEvent.value = PAIR_BRS_OPEN to it.url

                    break
                }

                ++i
            }
        }
    }

    fun realtimeIssueExtend() {
        commandEvent.value = CMD_REALTIME_ISSUE_FRAGMENT
    }
}
