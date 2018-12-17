package com.example.clone_daum.model.local

import androidx.room.*
import com.example.clone_daum.model.SearchRecyclerType
import com.example.clone_daum.model.ISearchRecyclerData
import com.example.common.IRecyclerDiff
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 3. <p/>
 *
 * https://developer.android.com/training/data-storage/room/accessing-data
 * https://medium.com/androiddevelopers/room-rxjava-acb0cd4f3757
 *
 *
 */


@Database(entities = [SearchHistory::class
    , PopularKeyword::class
    , UrlHistory::class
    , MyFavorite::class
], version = 1)
abstract class LocalDb: RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun popularKeywordDao(): PopularKeywordDao
    abstract fun urlHistoryDao(): UrlHistoryDao
    abstract fun myFavoriteDao(): MyFavoriteDao
}