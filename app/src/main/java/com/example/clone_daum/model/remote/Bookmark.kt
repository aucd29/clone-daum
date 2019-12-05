package com.example.clone_daum.model.remote

import androidx.room.PrimaryKey
import brigitte.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-05 <p/>
 */

data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val title: String,
    val url: String,
    val thumbnail: String,
    val tag: String
): IRecyclerDiff {
    override fun itemSame(item: IRecyclerDiff): Boolean =
        this._id == (item as Bookmark)._id

    override fun contentsSame(item: IRecyclerDiff): Boolean =
        this.url == (item as Bookmark).url
}