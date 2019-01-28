package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.model.remote.RealtimeIssueParser
import com.example.common.CommandEventViewModel
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 8. <p/>
 *
 * ApiHub.class 에 RealTimeIssueService 가 존재하는데 보이질 않네 그려
 */

class RealtimeIssueViewModel @Inject constructor(app: Application
    , val preConfig: PreloadConfig
) : CommandEventViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueViewModel::class.java)

        const val INTERVAL     = 7L
        const val CMD_BRS_OPEN = "brs-open"
    }

    private var mAllIssueList: List<RealtimeIssue>? = null
    private var mRealtimeCount = 0

    var mRealtimeIssueList: List<Pair<String, List<RealtimeIssue>>>? = null

    val dp                = CompositeDisposable()
    val tabAdapter        = ObservableField<RealtimeIssueTabAdapter>()
    val viewpager         = ObservableField<ViewPager>()
    val realtimeIssueText = ObservableField<String>()

    val visibleProgress   = ObservableInt(View.VISIBLE)

    fun load(html: String) {
        dp.add(Observable.just(html)
            .observeOn(Schedulers.io())
            .map (::parseRealtimeIssue)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                visibleProgress.set(View.GONE)

                mRealtimeIssueList = it
                mAllIssueList      = it.get(0).second

                startRealtimeIssue()
            })
    }

    private fun parseRealtimeIssue(main: String): List<Pair<String, List<RealtimeIssue>>> {
        if (mLog.isDebugEnabled) {
            mLog.debug("PARSE REALTIME ISSUE")
        }

        val parse = RealtimeIssueParser()
        val f = main.indexOf("""<div id="footerHotissueRankingDiv_channel_news1">""")
        val e = main.indexOf("""<div class="d_foot">""")

        if (f == -1 || e == -1) {
            mLog.error("ERROR: INVALID HTML DATA f = $f, e = $e")

            return parse.realtimeIssueList
        }

        // https://www.w3schools.com/tags/ref_urlencode.asp
        var issue = main.substring(f, e)
            .replace(" class='keyissue_area '", "")
            .replace(" class='keyissue_area on'", "")
            .replace("(\n|\t)".toRegex(), "")
            .replace("&amp;", "%26")
            .replace("&", "%26")
        issue = issue.substring(0, issue.length - "</div>".length)

        parse.loadXml(issue)

        if (mLog.isDebugEnabled) {
            parse.realtimeIssueList.forEach({
                mLog.debug("${it.first} : (${it.second.size})")
            })
        }

        return parse.realtimeIssueList
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

            realtimeIssueText.set("${index + 1} ${issue.text}")

            dp.add(Observable.interval(INTERVAL, TimeUnit.SECONDS).repeat().subscribe {
                val index = mRealtimeCount % list.size
                val issue = list.get(index)

                realtimeIssueText.set("${index + 1} ${issue.text}")
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

    fun openIssue(text: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("openIssue : $text")
        }

        val newText = text.replace("[0-9]".toRegex(), "").trim()

        mAllIssueList?.let { list ->
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
}