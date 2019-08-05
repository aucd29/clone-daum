package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
//import com.example.clone_daum.model.remote.RealtimeIssueParser
import com.example.clone_daum.model.remote.RealtimeIssueType
import brigitte.*
import brigitte.bindingadapter.AnimParams
import com.google.android.material.tabs.TabLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 8. <p/>
 *
 * ApiHub.class 에 RealTimeIssueService 가 존재하는데 보이질 않네 그려
 */

class RealtimeIssueViewModel @Inject constructor(
    app: Application,
    val preConfig: PreloadConfig
) : CommandEventViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueViewModel::class.java)

        const val ANIM_DURATION     = 300L
        const val INTERVAL          = 7L

        const val CMD_BRS_OPEN      = "brs-open"
        const val CMD_ANIM_FINISH   = "anim-finish"
        const val CMD_LOADED_ISSUE  = "loaded-realtime-issue"
        const val CMD_CLOSE_ISSUE   = "close-realtime-issue"
    }

    private var mAllIssueList: List<RealtimeIssue>? = null
    private var mRealtimeCount = 0
    private val mDisposable = CompositeDisposable()

    var mRealtimeIssueList: List<Pair<String, List<RealtimeIssue>>>? = null

    val tabChangedCallback = ObservableField<TabSelectedCallback>()
    var tabChangedLive     = MutableLiveData<TabLayout.Tab?>()

    val currentIssue    = ObservableField<RealtimeIssue>()


    val containerTransY = ObservableField<AnimParams>()
//    val dimmingBgAlpha  = ObservableField<AnimParams>()
    val tabMenuRotation = ObservableField<AnimParams>()
    val tabAlpha        = ObservableField<AnimParams>()
    val bgAlpha         = ObservableField<AnimParams>()
    val backgroundAlpha = ObservableField<AnimParams>()

//    val viewPagerLoaded = ObservableField<(() -> Unit)?>()
    val enableClick     = ObservableBoolean(false)

    val layoutTranslationY = ObservableField<Float>()

    val viewProgress      = ObservableInt(View.VISIBLE)
    val viewRealtimeIssue = ObservableInt(View.GONE)


    init {
        tabChangedCallback.set(TabSelectedCallback {
            if (mLog.isDebugEnabled) {
                mLog.debug("CHANGED ISSUE TAB ${it?.position}")
            }

            tabChangedLive.value = it
        })
    }

    fun load(html: String) {
        mDisposable.add(Observable.just(html)
            .subscribeOn(Schedulers.io())
            .map (::parseRealtimeIssue)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                viewProgress.gone()

                mRealtimeIssueList = it
                mAllIssueList      = it[0].second

                enableClick.set(true)
                command(CMD_LOADED_ISSUE)
                startRealtimeIssue()
            }, { errorLog(it, mLog) }))
    }

    private fun parseRealtimeIssue(main: String): List<Pair<String, List<RealtimeIssue>>> {
        if (mLog.isDebugEnabled) {
            mLog.debug("PARSE REALTIME ISSUE")
        }

        val fText = "var hotissueData = include("
        val eText = ");"
        val f = main.indexOf(fText) + fText.length
        val e = main.indexOf(eText, f)

        val issueList = arrayListOf<Pair<String, List<RealtimeIssue>>>()
        if (f == fText.length || e == -1) {
            mLog.error("ERROR: INVALID HTML DATA f = $f, e = $e")

            return issueList
        }

        val issue = main.substring(f, e)
            .replace("(\n\t)".toRegex(), "")
            .replace("  ", "")

        val realtimeIssueList: List<RealtimeIssueType> = issue.jsonParse()
        realtimeIssueList.forEach {
            val type = when (it.type) {
                "hotissue" -> "실시간이슈"
                "news"     -> "뉴스"
                "enter"    -> "연예"
                else       -> "스포츠"
            }

            if (mLog.isDebugEnabled) {
                mLog.debug("ISSUE : $type (${it.list.size})")
            }

            issueList.add(type to it.list)
        }

        return issueList
    }

    fun issueList(position: Int) =
        mRealtimeIssueList?.get(position)?.second

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // REALTIME ISSUE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun startRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("START REALTIME ISSUE")
        }

        mAllIssueList?.let { list ->
            val index = mRealtimeCount % list.size
            val issue = list[index]

            currentIssue.set(issue)

            mDisposable.add(Observable.timer(INTERVAL, TimeUnit.SECONDS).subscribe {
                val index = (it % list.size).toInt()
                val issue = list[index]

                currentIssue.set(issue)
//                ++mRealtimeCount

                if (mLog.isTraceEnabled) {
                    mLog.trace("TIMER EXPLODE $it ${issue.text} ")
                }
            })
        }
    }

    fun stopRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("STOP REALTIME ISSUE")
        }

        mDisposable.clear()
    }

    fun disposeRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("DISPOSE REALTIME ISSUE")
        }

        mDisposable.dispose()
    }

    fun titleConvert(issue: RealtimeIssue?): String {
        return issue?.run { "$index $text" } ?: ""
    }

    fun typeConvert(issue: RealtimeIssue?) = issue?.run {
            when (type) {
                "+" -> "<font color='red'>↑</font> $value"
                "-" -> "<font color='blue'>↓</font> $value"
                else -> "<font color='red'>NEW</font>"
            }.html()
        } ?: "".html()

    fun layoutTranslationY(h: Float) {
        if (mLog.isDebugEnabled) {
            mLog.debug("REALTIME ISSUE VIEWPAGER TRANSLATON Y : $h")
        }

        layoutTranslationY.set(h)
    }
}