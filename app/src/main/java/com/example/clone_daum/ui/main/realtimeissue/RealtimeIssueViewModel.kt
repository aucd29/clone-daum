package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import androidx.databinding.ObservableField
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 8. <p/>
 *
 * ApiHub.class 에 RealTimeIssueService 가 존재하는데 보이질 않네 그려
 */

class RealtimeIssueViewModel @Inject constructor(app: Application
    , val preConfig: PreloadConfig
    , val disposable: CompositeDisposable
) : RecyclerViewModel<RealtimeIssue>(app), IFinishFragmentAware, ICommandEventAware {

    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueViewModel::class.java)

        const val K_ISSUE_ALL   = "전체 이슈검색어"
        const val K_ISSUE_NEWS  = "뉴스 이슈검색어"
        const val K_ISSUE_ENTER = "연예 이슈검색어"
        const val K_ISSUE_SPORT = "스포츠 이슈검색어"

        const val CMD_BRS_OPEN  = "brs-open"
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override val finishEvent  = SingleLiveEvent<Void>()
    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()


    val tabAdapter  = ObservableField<RealtimeIssueTabAdapter>()
    val viewpager   = ObservableField<ViewPager>()


    fun type(type: String) {
        initAdapter("realtime_issue_child_item")

        when (type) {
            K_ISSUE_ALL,
            K_ISSUE_ENTER,
            K_ISSUE_NEWS,
            K_ISSUE_SPORT -> {
                val list = preConfig.realtimeIssueMap.get(type)

                if (mLog.isDebugEnabled) {
                    mLog.debug("TYPE: $type\n${list.toString()}")
                }

                items.set(list)
            }
        }
    }

    override fun commandEvent(cmd: String, data: Any?) {
        super.commandEvent(cmd, data)
    }
}