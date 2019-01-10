package com.example.clone_daum.model.remote

import com.example.common.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 9. <p/>
 */

data class RealtimeIssue (
    val index: Int,
    val url: String,
    var text: String
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff): Boolean {
        val newItem = item as RealtimeIssue
        return url == newItem.url && text == newItem.text
    }
}