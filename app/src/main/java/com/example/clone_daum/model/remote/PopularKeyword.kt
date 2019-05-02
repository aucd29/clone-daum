package com.example.clone_daum.model.remote

import brigitte.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 21. <p/>
 */

data class PopularKeyword (val keyword: String
    , val url: String
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff): Boolean {
        val newItem = item as PopularKeyword
        return keyword == newItem.keyword && url == newItem.url
    }
}

data class PopularItemList (
    val item: List<PopularKeyword>
)

data class PopularSearchedWord (
    val items: List<PopularItemList>
)