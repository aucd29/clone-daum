package com.example.clone_daum.model.local

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.clone_daum.model.*
import com.example.common.*
import java.io.Serializable

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
    @Ignore override var type: Int = SearchRecyclerType.T_HISTORY

    override fun compare(item: IRecyclerDiff)= this._id == (item as SearchHistory)._id
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

@Entity(tableName = "urlHistory")
data class UrlHistory (
    val title: String,
    val url: String?,
    val date: Long?,
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0
) : IRecyclerDiff, IRecyclerItem, IDateCalculator, IRecyclerExpandable<UrlHistory> {
    companion object {
        const val T_HISTORY   = 0
        const val T_SEPERATOR = 1
    }

    @Ignore override var type: Int = T_HISTORY
    @Ignore override var toggle = false
    @Ignore override var childList: List<UrlHistory> = arrayListOf()

    override fun compare(item: IRecyclerDiff)= this._id == (item as UrlHistory)._id
    override fun timeInMillis() = date?.let { it } ?: 0
}

@Entity(tableName = "myFavorite")
data class MyFavorite (
    /** 링크 명 or 폴더 명 */
    var name: String,
    /** 링크 (url) */
    val url: String = "",
    /** 링크일 때 사용 될 폴더 */
    var folder: String = "",
    /** 타입 (링크: 0, 폴더: 1) */
    val favType: Int = T_DEFAULT,
    /** 날짜 인데 이를 기준으로 order 처리 한다. */
    var date: Long = System.currentTimeMillis(),
    /** id 값 */
    @PrimaryKey(autoGenerate = true)
    var _id: Int       = 0
) : IRecyclerDiff, IRecyclerItem, IRecyclerPosition, Serializable {
    constructor(fav: MyFavorite)
        : this(fav.name, fav.url, fav.folder, fav.favType,fav.date, fav._id)

    companion object {
        const val T_FOLDER  = 0
        const val T_DEFAULT = 1
    }

    @Ignore var check = ObservableBoolean(false)
    @Ignore override var type: Int = favType
    @Ignore override var position: Int = 0

    override fun compare(item: IRecyclerDiff) = this._id == (item as MyFavorite)._id

    fun swapDate(fav: MyFavorite) {
        val tmp = date
        date = fav.date
        fav.date = tmp
    }
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