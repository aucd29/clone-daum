package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.model.remote.RealtimeIssueType
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.viewmodel.LifecycleCommandEventViewModel
import com.google.android.material.tabs.TabLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 8. <p/>
 *
 * ApiHub.class 에 RealTimeIssueService 가 존재하는데 보이질 않네 그려
 */

class RealtimeIssueViewModel @Inject constructor(
    val preConfig: PreloadConfig,
    app: Application
) : LifecycleCommandEventViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueViewModel::class.java)

        const val ANIM_DURATION     = 300L
        const val INTERVAL          = 7000L

        const val CMD_BRS_OPEN      = "brs-open"
        const val CMD_CLOSE_ISSUE   = "close-realtime-issue"
        const val CMD_LOADED_ISSUE  = "loaded-realtime-issue"
        const val ITN_RELOAD_ISSUE  = "reload-realtime-issue"

        const val ITN_ERROR_ISSUE   = "error-realtime-issue"
    }

    private var mAllIssueList: List<RealtimeIssue>? = null
    private val mDisposable = CompositeDisposable()

    var mRealtimeIssueList: List<Pair<String, List<RealtimeIssue>>>? = null

    val tabChangedCallback = ObservableField<TabSelectedCallback>()
    var tabChangedLive     = MutableLiveData<TabLayout.Tab?>()
    val currentIssue       = ObservableField<RealtimeIssue>()

    val containerTransY    = ObservableField<AnimParams>()
    val tabMenuRotation    = ObservableField<AnimParams>()
    val tabAlpha           = ObservableField<AnimParams>()
    val bgAlpha            = ObservableField<AnimParams>()
    val backgroundAlpha    = ObservableField<AnimParams>()

    val layoutTranslationY = ObservableField<Float>()
    val enableClick        = ObservableBoolean(false)

    val viewIssueProgress  = ObservableInt(View.VISIBLE)
    val viewRealtimeIssue  = ObservableInt(View.GONE)
    val viewRetry          = ObservableInt(View.GONE)

    val htmlDataLive       = MutableLiveData<String>()

    init {
        tabChangedCallback.set(TabSelectedCallback {
            if (mLog.isDebugEnabled) {
                mLog.debug("CHANGED ISSUE TAB ${it?.position}")
            }

            tabChangedLive.value = it
        })
    }

    fun loadData() {
        mDisposable.add(preConfig.daumMain()
            .subscribe({ html ->
                htmlDataLive.postValue(html)
                load(html)
            }, {
                errorLog(it, mLog)
            }))
    }

    fun load(html: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("LOAD REALTIME ISSUE")
        }

        if (html.isEmpty()) {
            mLog.error("ERROR: INVALID REALTIME ISSUE DATA")

            command(ITN_ERROR_ISSUE)
            return
        }

        mDisposable.add(Observable.just(html)
            .subscribeOn(Schedulers.io())
            .map (::parseRealtimeIssue)
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                if (it.isEmpty()) {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("PARSING ERROR")
                    }

                    command(ITN_ERROR_ISSUE)

                    false
                } else true
            }
            .subscribe ({
                if (mLog.isDebugEnabled) {
                    mLog.debug("SUBSCRIBE REALTIME ISSUE")
                }

                viewIssueProgress.gone()
                viewRetry.gone()

                mRealtimeIssueList = it
                mAllIssueList      = it[0].second

                enableClick.set(true)
                startRealtimeIssue()

                command(CMD_LOADED_ISSUE)
            }, {
                errorLog(it, mLog)

                command(ITN_ERROR_ISSUE)
            }))
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

    private fun startRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("START REALTIME ISSUE")
        }

        mAllIssueList?.let { list ->
            mDisposable.clear()
            mDisposable.add(interval(INTERVAL, initDelay = 0)
                .map {
                    if (mLog.isTraceEnabled) {
                        mLog.trace("REALTIME ISSUE INTERVAL $it")
                    }

                    list[(it % list.size).toInt()]
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { currentIssue.set(it) })
        }
    }

    private fun stopRealtimeIssue() {
        if (mLog.isDebugEnabled) {
            mLog.debug("STOP REALTIME ISSUE")
        }

        mDisposable.clear()
    }

    override fun onCleared() {
        mDisposable.dispose()
        super.onCleared()
    }

    fun titleConvert(issue: RealtimeIssue?): String {
        return issue?.run { "$index $text" } ?: ""
    }

    fun typeConvert(issue: RealtimeIssue?) = issue?.run {
            when (type) {
                "+"  -> "<font color='red'>↑</font> $value"
                "-"  -> "<font color='blue'>↓</font> $value"
                else -> "<font color='red'>NEW</font>"
            }.html()
        } ?: "".html()

    fun layoutTranslationY(h: Float) {
        if (mLog.isDebugEnabled) {
            mLog.debug("REALTIME ISSUE VIEWPAGER TRANSLATON Y : $h")
        }

        layoutTranslationY.set(h)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // LifecycleCommandEventViewModel
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE   -> stopRealtimeIssue()
            Lifecycle.Event.ON_RESUME  -> startRealtimeIssue()
            else -> {}
        }
    }

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            ITN_ERROR_ISSUE -> {
                viewIssueProgress.gone()
                viewRetry.visible()
            }
            ITN_RELOAD_ISSUE -> {
                viewIssueProgress.visible()
                viewRetry.gone()

                loadData()
            }
            else -> {
                super.command(cmd, data)
            }
        }
    }
}