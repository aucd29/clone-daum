package com.example.clone_daum.model.local

import androidx.annotation.IntDef
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.clone_daum.model.*
import brigitte.*
import java.io.File
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

    override fun itemSame(item: IRecyclerDiff): Boolean =
        if (item is SearchHistory) _id == item._id
        else false

    override fun contentsSame(item: IRecyclerDiff)=
        this._id == (item as SearchHistory)._id
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
    override fun itemSame(item: IRecyclerDiff): Boolean  =
        _id == (item as Zzim)._id

    override fun contentsSame(item: IRecyclerDiff) =
        this._id == (item as Zzim)._id
}

@Entity(tableName = "urlHistory")
data class UrlHistory (
    val title: String,
    val url: String?,
    val date: Long?,
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0
) : IDateCalculator, IRecyclerExpandable<UrlHistory> {
    companion object {
        const val T_HISTORY   = 0
        const val T_SEPERATOR = 1
    }

    @Ignore override var type           = T_HISTORY
    @Ignore override var status         = ObservableBoolean(false)
    @Ignore override var timeInMillis   = date?.let { it } ?: 0
    @Ignore override var childList: List<UrlHistory> = arrayListOf()
    @Ignore val check = ObservableBoolean(false)

    override fun itemSame(item: IRecyclerDiff): Boolean =
        _id == (item as UrlHistory)._id

    override fun contentsSame(item: IRecyclerDiff)=
        this._id == (item as UrlHistory)._id
}

@Entity(tableName = "myFavorite")
data class MyFavorite (
    /** 링크 명 or 폴더 명 */
    var name: String,
    /** 링크 (url) */
    val url: String = "",
    /** 링크일 때 사용 될 폴더 */
    var folderId: Int = 0,
    /** 타입 (링크: 0, 폴더: 1) */
    val favType: Int = T_DEFAULT,
    /** 날짜 인데 이를 기준으로 order 처리 한다. */
    var date: Long = System.currentTimeMillis(),
    /** id 값 */
    @PrimaryKey(autoGenerate = true)
    var _id: Int       = 0
) : IRecyclerDiff, IRecyclerItem, IRecyclerPosition, Serializable {
    constructor(fav: MyFavorite)
        : this(fav.name, fav.url, fav.folderId, fav.favType,fav.date, fav._id)

    companion object {
        const val T_FOLDER  = 0
        const val T_DEFAULT = 1
    }

    @Ignore var check = ObservableBoolean(false)
    @Ignore override var type: Int = favType
    @Ignore override var position: Int = 0

    override fun itemSame(item: IRecyclerDiff): Boolean =
        _id == (item as MyFavorite)._id

    override fun contentsSame(item: IRecyclerDiff) =
        this._id == (item as MyFavorite)._id

    fun swapDate(fav: MyFavorite) {
        val tmp = date
        date     = fav.date
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
    override fun itemSame(item: IRecyclerDiff): Boolean =
        _id == (item as FrequentlySite)._id

    override fun contentsSame(item: IRecyclerDiff)=
        this.url == (item as FrequentlySite).url
}


@IntDef(value = [SettingType.T_CATEGORY, SettingType.T_NORMAL, SettingType.T_COLOR,
    SettingType.T_SWITCH, SettingType.T_CHECK, SettingType.T_DEPTH, SettingType.T_DAUM])
annotation class SettingTypeDef

data class SettingType(
    val _id: Int,
    val title: String,
    val summary: String? = null,
    val checked: MutableLiveData<Boolean> = MutableLiveData(true),
    val enabled: MutableLiveData<Boolean> = MutableLiveData(true),
    val option: MutableLiveData<String>? = null,
    @SettingTypeDef override var type: Int = T_NORMAL
): IRecyclerDiff, IRecyclerItem, IRecyclerPosition {
    companion object {
        const val T_CATEGORY = 0
        const val T_NORMAL   = 1
        const val T_COLOR    = 2
        const val T_SWITCH   = 3
        const val T_CHECK    = 4
        const val T_DEPTH    = 5
        const val T_DAUM     = 6
    }

    override var position: Int = 0

    override fun itemSame(item: IRecyclerDiff): Boolean =
        _id == (item as SettingType)._id

    override fun contentsSame(item: IRecyclerDiff): Boolean =
        this.title         == (item as SettingType).title &&
        this.summary       == item.summary &&
        this.checked.value == item.checked.value &&
        this.enabled.value == item.enabled.value &&
        this.option?.value == item.option?.value
}

data class HomeMenu(
    val _id: Int,
    val title: String,
    val order: Int
) : IRecyclerDiff {
    override fun itemSame(item: IRecyclerDiff): Boolean =
        _id == (item as HomeMenu)._id

    override fun contentsSame(item: IRecyclerDiff): Boolean =
        this.title == (item as HomeMenu).title && order == item.order
}

data class AlarmHistory(
    val _id: Int,
    val title: String,
    val date: Long
) : IRecyclerDiff {
    override fun itemSame(item: IRecyclerDiff): Boolean =
        _id == (item as AlarmHistory)._id

    override fun contentsSame(item: IRecyclerDiff): Boolean =
        this.title == (item as AlarmHistory).title && date == item.date
}

data class FileInfo(
    val _id: Int,
    val fp: File,
    override var type: Int = T_DIR
) : IRecyclerDiff, IRecyclerItem {
    companion object {
        const val T_UP  = 0
        const val T_DIR = 1
    }

    override fun itemSame(item: IRecyclerDiff): Boolean =
        _id == (item as FileInfo)._id

    override fun contentsSame(item: IRecyclerDiff): Boolean =
        this.fp.name == (item as FileInfo).fp.name
}
