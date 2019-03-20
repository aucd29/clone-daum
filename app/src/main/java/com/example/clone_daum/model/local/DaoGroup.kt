package com.example.clone_daum.model.local

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
 *
 * https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1
 * https://codinginfinite.com/android-room-persistent-rxjava/
 *
 * insert 시 call 하려면
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    순서대로 요청해야 함

   예)
   mFavoriteDao.insert(MyFavorite(name, url, folder))
     .subscribeOn(Schedulers.io())
     .observeOn(AndroidSchedulers.mainThread())
     .subscribe(::finishEvent) {
         if (mLog.isDebugEnabled) {
            it.printStackTrace()
         }
         mLog.error("ERROR: ${it.message}")
         snackbar(it)
   })
 */

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM searchHistory ORDER BY _id DESC LIMIT 10")
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
interface ZzimDao {
    @Query("SELECT * FROM zzim")
    fun list(): Flowable<List<Zzim>>

    @Query("SELECT COUNT(*) FROM zzim WHERE url=:url")
    fun hasUrl(url: String): Maybe<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keyword: Zzim): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(keyword: List<Zzim>): Completable

    @Update
    fun update(keyword: Zzim): Completable

    @Delete
    fun delete(keyword: Zzim): Completable

    @Query("DELETE FROM zzim")
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
    @Query("SELECT * FROM myFavorite WHERE folder='' ORDER BY favType ASC, _id ASC")
    fun selectMain(): Flowable<List<MyFavorite>>

    @Query("SELECT * FROM myFavorite WHERE folder=:folderName ORDER BY _id ASC")
    fun selectByFolderName(folderName: String): Flowable<List<MyFavorite>>

    @Query("SELECT * FROM myFavorite WHERE favType=:favType ORDER BY _id DESC")
    fun selectFolder(favType: Int = MyFavorite.T_FOLDER): Flowable<List<MyFavorite>>

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

@Dao
interface FrequentlySiteDao {
    @Query("SELECT * FROM frequentlySite ORDER BY count DESC LIMIT 4")
    fun select(): Flowable<List<FrequentlySite>>

    @Query("SELECT COUNT(*) FROM frequentlySite WHERE url=:url")
    fun hasUrl(url: String): Maybe<Int>

    @Query("SELECT COUNT(*) FROM frequentlySite")
    fun count(): Maybe<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(site: FrequentlySite): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(site: List<FrequentlySite>): Completable

//    @Update
//    fun update(keyword: FrequentlySite): Completable

    @Delete
    fun delete(keyword: FrequentlySite): Completable

    @Query("DELETE FROM frequentlySite")
    fun deleteAll()
}
