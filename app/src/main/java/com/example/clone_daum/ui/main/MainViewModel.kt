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
import com.example.common.WebViewEvent
import com.example.common.WebViewSettingParams
import com.example.common.arch.SingleLiveEvent
import com.example.common.string
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainViewModel @Inject constructor(val app: Application
    , val preConfig: PreloadConfig
    , val disposable: CompositeDisposable
) : AndroidViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1
    }

    val tabAdapter         = ObservableField<MainTabAdapter>()
    val viewpager          = ObservableField<ViewPager>()
    val brsSetting         = ObservableField<WebViewSettingParams>()
    val brsEvent           = ObservableField<WebViewEvent>()
    val viewpagerPageLimit = ObservableInt(3)
    val visibleBack        = ObservableInt(View.GONE)
    var gotoNewsEvent      = ObservableInt(0)
    val gotoSearchEvent    = SingleLiveEvent<Void>()
    val navEvent           = SingleLiveEvent<Void>()

    // viewpager 에 adapter 가 set 된 이후 시점을 알려줌 (ViewPagerBindingAdapter)
    val viewpagerLoadedEvent     = ObservableField<() -> Unit>()
    val appbarOffsetChangedEvent = ObservableField<(AppBarLayout, Int) -> Unit>()
    val appbarOffsetLiveEvent    = MutableLiveData<Int>()

    val realtimeIssueText        = ObservableField<String>()
    var realtimeCount            = 0
    val brsOpenEvent             = SingleLiveEvent<String>()

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
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }

        gotoSearchEvent.call()
    }

    fun searchExtendMenu() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun searchExtendRank() {
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

    fun tabMenu() {
        navEvent.call()
    }

    fun webviewBack() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun startRealtimeIssue() {
        if (preConfig.realtimeIssue.size == 0) {
            realtimeIssueText.set(string(R.string.main_realtime_issue_network_error))
            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("START REALTIME ISSUE")
        }

        val index = realtimeCount % preConfig.realtimeIssue.size
        val issue = preConfig.realtimeIssue.get(index)

        realtimeIssueText.set("${index + 1} ${issue.text}")

        disposable.add(Observable.interval(5, TimeUnit.SECONDS).repeat().subscribe {
            val index = realtimeCount % preConfig.realtimeIssue.size
            val issue = preConfig.realtimeIssue.get(index)

            realtimeIssueText.set("${index + 1} ${issue.text}")

            ++realtimeCount
            if (mLog.isDebugEnabled) {
                mLog.debug("TIMER EXPLODE $realtimeCount ${issue.text} ")
            }
        })
    }

    fun stopRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("STOP REALTIME ISSUE")
        }

        disposable.clear()
    }

    fun realtimeIssueOpenEvent(text: String) {
        preConfig.realtimeIssue.forEach {
            if (it.text == text) {
                brsOpenEvent.value = it.url
                return@forEach
            }
        }
    }
}
