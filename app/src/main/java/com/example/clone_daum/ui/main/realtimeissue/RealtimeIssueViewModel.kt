package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import android.text.Spanned
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
//import com.example.clone_daum.model.remote.RealtimeIssueParser
import com.example.clone_daum.model.remote.RealtimeIssueType
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
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

class RealtimeIssueViewModel @Inject constructor(app: Application
    , val preConfig: PreloadConfig
) : CommandEventViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueViewModel::class.java)

        const val ANIM_DURATION     = 400L
        const val INTERVAL          = 7L

        const val CMD_BRS_OPEN      = "brs-open"
        const val CMD_ANIM_FINISH   = "anim-finish"
        const val CMD_LOADED_ISSUE  = "loaded-realtime-issue"
    }

    private var mAllIssueList: List<RealtimeIssue>? = null
    private var mRealtimeCount = 0

    var mRealtimeIssueList: List<Pair<String, List<RealtimeIssue>>>? = null

    val dp              = CompositeDisposable()
    val tabAdapter      = ObservableField<RealtimeIssueTabAdapter>()
    val viewpager       = ObservableField<ViewPager>()
    val currentIssue    = ObservableField<RealtimeIssue>()
    val visibleProgress = ObservableInt(View.VISIBLE)

    val containerTransY = ObservableField<AnimParams>()
    val dimmingBgAlpha  = ObservableField<AnimParams>()
    val tabMenuRotation = ObservableField<AnimParams>()
    val tabAlpha        = ObservableField<AnimParams>()

    val visibleDetail   = ObservableField<Int>(View.GONE)


    fun load(html: String) {
        dp.add(Observable.just(html)
            .observeOn(Schedulers.io())
            .map (::parseRealtimeIssue)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                visibleProgress.set(View.GONE)

                mRealtimeIssueList = it
                mAllIssueList      = it.get(0).second

                commandEvent(CMD_LOADED_ISSUE)
                startRealtimeIssue()
            })
    }

    private fun parseRealtimeIssue(main: String): List<Pair<String, List<RealtimeIssue>>> {
        if (mLog.isDebugEnabled) {
            mLog.debug("PARSE REALTIME ISSUE")
        }

        val fText = "var hotissueData = include("
        val eText = ");"
        val f = main.indexOf(fText) + fText.length
        val e = main.indexOf(eText, f)

        val issueList: ArrayList<Pair<String, List<RealtimeIssue>>> = arrayListOf()
        if (f == fText.length || e == -1) {
            mLog.error("ERROR: INVALID HTML DATA f = $f, e = $e")

            return issueList
        }

        val issue = main.substring(f, e)
            .replace("(\n|\t)".toRegex(), "")
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
            val issue = list.get(index)

            currentIssue.set(issue)

            dp.add(Observable.interval(INTERVAL, TimeUnit.SECONDS).repeat().subscribe {
                val index = mRealtimeCount % list.size
                val issue = list.get(index)

                currentIssue.set(issue)
                ++mRealtimeCount

                if (mLog.isTraceEnabled) {
                    mLog.trace("TIMER EXPLODE $mRealtimeCount ${issue.text} ")
                }
            })
        }
    }

    fun stopRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("STOP REALTIME ISSUE")
        }

        dp.clear()
    }

    fun titleConvert(issue: RealtimeIssue?): String {
        return issue?.run { "${index} ${text}" } ?: ""
    }

    fun typeConvert(issue: RealtimeIssue?): Spanned {
        return issue?.run {
            when (type) {
                "+" -> "<font color='red'>↑</font> $value"
                "-" -> "<font color='blue'>↓</font> $value"
                else -> "<font color='red'>NEW</font> $value"
            }.html()
        } ?: "".html()
    }
}