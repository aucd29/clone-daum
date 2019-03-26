package com.example.clone_daum.model.local

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.clone_daum.model.*
import com.example.common.IRecyclerDiff
import com.example.common.IRecyclerItem
import com.example.common.IRecyclerPosition

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
    val url: String,
    val date: Long,
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0
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
    /** 링크 명 or 폴더 명 */
    val name: String,
    /** 링크 (url) */
    val url: String = "",
    /** 링크일 때 사용 될 폴더 */
    val folder: String = "",
    /** 타입 (링크: 0, 폴더: 1) */
    val favType: Int = T_DEFAULT,
    /** 날짜 (timestamp) */
    val date: Long = System.currentTimeMillis(),
    /** id 값 */
    @PrimaryKey(autoGenerate = true)
    val _id: Int       = 0
) : IRecyclerDiff, IRecyclerItem, IRecyclerPosition {
    @Ignore
    var pos: Int   = 0

    @Ignore
    var check: Boolean = false

    companion object {
        const val T_FOLDER  = 0
        const val T_DEFAULT = 1
    }

    override fun position(pos: Int) {
        this.pos = pos
    }
    override fun position() = pos

    override fun compare(item: IRecyclerDiff) = this._id == (item as MyFavorite)._id
    override fun type() = favType
}

@Entity(tableName = "frequentlySite")
data class FrequentlySite(
    val title: String,
    val url: String,
    val count: Long,
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff)=
        this.url == (item as FrequentlySite).url
}