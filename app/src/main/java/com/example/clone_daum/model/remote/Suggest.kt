package com.example.clone_daum.model.remote

import com.example.clone_daum.model.ISearchRecyclerData
import com.example.clone_daum.model.SearchRecyclerType
import brigitte.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

data class Suggest (
    val q: String,
    val subkeys: List<String>
)

// recycler 에 사용하기 위해 랩핑
data class SuggestItem (
    val keyword: String, val rawKeyword: String,
    override var type: Int = SearchRecyclerType.T_SUGGEST
) : ISearchRecyclerData {

    override fun itemSame(item: IRecyclerDiff): Boolean  =
        if (item is SuggestItem) this == item
        else false

    override fun contentsSame(item: IRecyclerDiff): Boolean {
        val nitem = item as SuggestItem
        return keyword == nitem.keyword
    }
}