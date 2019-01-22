package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.common.ICommandEventAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

class RealtimeIssueChildViewModel @Inject constructor(app: Application
) : RecyclerViewModel<RealtimeIssue>(app), ICommandEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueChildViewModel::class.java)
        const val CMD_BRS_OPEN = "brs-open"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()

    fun initAdapter(list: List<RealtimeIssue>) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT REALTIME RECYCLER ADAPTER")
        }

        initAdapter("realtime_issue_child_item")
        items.set(list)
    }
}