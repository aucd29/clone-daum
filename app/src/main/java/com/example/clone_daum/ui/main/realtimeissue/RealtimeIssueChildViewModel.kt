package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import android.text.Spanned
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.common.ICommandEventAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import com.example.common.html
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

class RealtimeIssueChildViewModel @Inject constructor(app: Application
) : RecyclerViewModel<RealtimeIssue>(app), ICommandEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueChildViewModel::class.java)

        const val CMD_BRS_OPEN = "brs-open"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()

    fun initRealtimeIssueAdapter(list: List<RealtimeIssue>) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT REALTIME RECYCLER ADAPTER")
        }

        initAdapter("realtime_issue_child_item")
        items.set(list)
    }

    fun typeConvert(issue: RealtimeIssue?): Spanned {
        return issue?.run {
            when (type) {
                "+"  -> "<font color='red'>↑</font> $value"
                "-"  -> "<font color='blue'>↓</font> $value"
                else -> "<font color='red'>NEW</font>"
            }.html()
        } ?: "".html()
    }
}