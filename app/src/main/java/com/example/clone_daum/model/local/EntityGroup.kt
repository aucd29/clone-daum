package com.example.clone_daum.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clone_daum.model.ISearchRecyclerData
import com.example.clone_daum.model.SearchRecyclerType
import com.example.common.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

@Entity(tableName = "searchHistory")
data class SearchHistory (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val keyword: String,
    val date: Long
) : ISearchRecyclerData {
    override fun compare(item: IRecyclerDiff)= this._id == (item as SearchHistory)._id
    override fun type() = SearchRecyclerType.T_HISTORY
}

@Entity(tableName = "popularKeyword")
data class PopularKeyword (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val keyword: String
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff)= this._id == (item as PopularKeyword)._id
}