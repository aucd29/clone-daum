package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import android.text.Spanned
import com.example.clone_daum.model.remote.RealtimeIssue
import brigitte.RecyclerViewModel
import brigitte.html
import com.example.clone_daum.R
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

class RealtimeIssueChildViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<RealtimeIssue>(app) {
    companion object {
        private val logger = LoggerFactory.getLogger(RealtimeIssueChildViewModel::class.java)

        const val CMD_BRS_OPEN = "brs-open"
    }

    fun initRealtimeIssueAdapter(list: List<RealtimeIssue>) {
        if (logger.isDebugEnabled) {
            logger.debug("INIT REALTIME RECYCLER ADAPTER")
        }

        items.set(list)
    }

    fun typeConvert(issue: RealtimeIssue?): Spanned? {
        return issue?.run {
            when (type) {
                "+"  -> "<font color='red'>↑</font> $value"
                "-"  -> "<font color='blue'>↓</font> $value"
                else -> "<font color='red'>NEW</font>"
            }.html()
        } ?: "".html()
    }
}