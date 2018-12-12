package com.example.clone_daum.model.remote

import com.example.clone_daum.model.ISearchRecyclerData
import com.example.clone_daum.model.SearchRecyclerType
import com.example.common.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

data class Suggest (
    val q: String,
    val subkeys: List<String>
)


// recycler 에 사용하기 위해 랩핑
data class SuggestItem (
    val keyword: String
) : ISearchRecyclerData {
    override fun compare(item: IRecyclerDiff): Boolean {
        val nitem = item as SuggestItem
        return keyword == nitem.keyword
    }

    override fun type() = SearchRecyclerType.T_SUGGEST
}