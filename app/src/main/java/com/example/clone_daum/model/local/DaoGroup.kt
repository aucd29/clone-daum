package com.example.clone_daum.model.local

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM searchHistory ORDER BY _id DESC")
    fun search(): Flowable<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keyword: SearchHistory): Completable

    @Update
    fun update(keyword: SearchHistory): Completable

    @Delete
    fun delete(keyword: SearchHistory): Completable

    @Query("DELETE FROM searchHistory")
    fun deleteAll()
}

@Dao
interface PopularKeywordDao {
    @Query("SELECT * FROM popularKeyword")
    fun list(): Flowable<List<PopularKeyword>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keyword: PopularKeyword): Completable

    @Update
    fun update(keyword: PopularKeyword): Completable

    @Delete
    fun delete(keyword: PopularKeyword): Completable

    @Query("DELETE FROM popularKeyword")
    fun deleteAll()
}

@Dao
interface UrlHistoryDao {
    @Query("SELECT * FROM urlHistory ORDER BY _id DESC")
    fun search(): Flowable<List<UrlHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keyword: UrlHistory): Completable

    @Update
    fun update(keyword: UrlHistory): Completable

    @Delete
    fun delete(keyword: UrlHistory): Completable

    @Query("DELETE FROM urlHistory")
    fun deleteAll()
}


@Dao
interface MyFavoriteDao {
    @Query("SELECT * FROM myFavorite ORDER BY _id DESC")
    fun select(): Flowable<List<MyFavorite>>

    @Query("SELECT COUNT(*) FROM myFavorite WHERE url=:url")
    fun hasUrl(url: String): Maybe<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keyword: MyFavorite): Completable

    @Update
    fun update(keyword: MyFavorite): Completable

    @Delete
    fun delete(keyword: MyFavorite): Completable

    @Query("DELETE FROM myFavorite")
    fun deleteAll()
}
