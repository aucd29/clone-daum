package com.example.clone_daum.model.remote

import com.example.common.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 21. <p/>
 */

data class PopularKeyword (
    val keyword: String
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff): Boolean {
        val newItem = item as PopularKeyword
        return keyword == newItem.keyword
    }
}