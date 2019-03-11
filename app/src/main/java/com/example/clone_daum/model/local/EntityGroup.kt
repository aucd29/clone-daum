package com.example.clone_daum.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clone_daum.model.*
import com.example.common.IRecyclerDiff
import com.example.common.IRecyclerItem

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
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

@Entity(tableName = "zzim")
data class Zzim (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val title: String,
    val screenshot: String = "",
    val url: String,
    val tags: String = "",
    val locked: Boolean = false
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff) = this._id == (item as Zzim)._id
}
//
//@Entity(tableName = "zzimTag")
//data class ZzimTag (
//    @PrimaryKey(autoGenerate = true)
//    val _id: Int = 0,
//    val parent: Int,
//    val tag: String
//) : IRecyclerDiff {
//    override fun compare(item: IRecyclerDiff) = this._id == (item as ZzimTag)._id
//}

//// https://stackoverflow.com/questions/45059942/return-type-for-android-room-joins
//data class ZzimAndTag (
//    @Embedded
//    val zzim: Zzim,
//    @Relation(parentColumn = "Zzim._id", entityColumn = "ZzimTag._id")
//    val tags: List<ZzimTag>
//)

@Entity(tableName = "urlHistory")
data class UrlHistory (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val url: String,
    val date: Long
) : IRecyclerDiff, IRecyclerItem {
    companion object {
        const val T_HISTORY   = 0
        const val T_SEPERATOR = 1
    }

    override fun compare(item: IRecyclerDiff)= this._id == (item as UrlHistory)._id
    override fun type() = T_HISTORY
}

@Entity(tableName = "myFavorite")
data class MyFavorite (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val title: String,
    val url: String,
    val date: Long,
    val folder: String = "",
    val favType: Int = T_DEFAULT
) : IRecyclerDiff, IRecyclerItem {
    companion object {
        const val T_DEFAULT = 0
        const val T_FOLDER  = 1
    }

    override fun compare(item: IRecyclerDiff) = this._id == (item as MyFavorite)._id
    override fun type() = favType
}

@Entity(tableName = "frequentlySite")
data class FrequentlySite(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val title: String,
    val url: String,
    val count: Long
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff)=
        this.url == (item as FrequentlySite).url
}