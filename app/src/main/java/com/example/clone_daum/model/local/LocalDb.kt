package com.example.clone_daum.model.local

import androidx.room.*

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 3. <p/>
 *
 * https://developer.android.com/training/data-storage/room/accessing-data
 * https://medium.com/androiddevelopers/room-rxjava-acb0cd4f3757
 */

@Database(entities = [ SearchHistory::class
    , UrlHistory::class
    , MyFavorite::class
    , FrequentlySite::class
    , Zzim::class
], version = 1)
abstract class LocalDb: RoomDatabase() {
    //    abstract fun popularKeywordDao(): PopularKeywordDao

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun myFavoriteDao(): MyFavoriteDao
    abstract fun frequentlySiteDao(): FrequentlySiteDao
    abstract fun zzimDao(): ZzimDao
    abstract fun urlHistoryDao(): UrlHistoryDao
}