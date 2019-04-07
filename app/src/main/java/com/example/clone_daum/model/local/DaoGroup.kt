package com.example.clone_daum.model.local

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

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
        .subscribe(::finish) {
            if (mLog.isDebugEnabled) {
                it.printStackTrace()
            }
            mLog.error("ERROR: ${it.message}")
            snackbar(it)
        })

 * Flowable 는 디비가 변경되면 자동으로 subscribe 됨

 * DELETE QUERY 는 Completable.fromAction 에서 동작 시키면 됨
   예)
    Completable.fromAction { mFavoriteDao.delete() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {

        }
 */

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM searchHistory ORDER BY _id DESC LIMIT 10")
    fun search(): Flowable<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: SearchHistory): Completable

    @Update
    fun update(data: SearchHistory): Completable

    @Delete
    fun delete(data: SearchHistory): Completable

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
    fun insert(data: Zzim): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Zzim>): Completable

    @Update
    fun update(data: Zzim): Completable

    @Delete
    fun delete(data: Zzim): Completable

    @Query("DELETE FROM zzim")
    fun deleteAll()
}


@Dao
interface UrlHistoryDao {
    @Query("SELECT * FROM urlHistory ORDER BY _id DESC")
    fun search(): Flowable<List<UrlHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: UrlHistory): Completable

    @Update
    fun update(data: UrlHistory): Completable

    @Delete
    fun delete(data: UrlHistory): Completable

    @Query("DELETE FROM urlHistory")
    fun deleteAll()
}


@Dao
interface MyFavoriteDao {
    @Query("SELECT * FROM myFavorite WHERE folder='' ORDER BY date ASC")
    fun selectShowAllFlowable(): Flowable<List<MyFavorite>>

    @Query("SELECT * FROM myFavorite WHERE favType=:favType ORDER BY date ASC")
    fun selectShowFolderFlowable(favType: Int = MyFavorite.T_FOLDER): Flowable<List<MyFavorite>>

    @Query("SELECT * FROM myFavorite WHERE folder='' ORDER BY date ASC")
    fun selectShowAll(): Single<List<MyFavorite>>

    @Query("SELECT * FROM myFavorite WHERE folder=:folderName ORDER BY date ASC")
    fun selectByFolderName(folderName: String): Flowable<List<MyFavorite>>

    @Query("SELECT COUNT(*) FROM myFavorite WHERE url=:url")
    fun hasUrl(url: String): Maybe<Int>

    @Query("SELECT COUNT(*) FROM myFavorite WHERE name=:folder")
    fun hasFolder(folder: String): Maybe<Int>

    @Query("SELECT COUNT(*) FROM myFavorite WHERE name=:folder AND _id != :id")
    fun hasFolder(folder: String, id: Int): Maybe<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: MyFavorite): Completable

    @Update
    fun update(vararg data: MyFavorite): Completable

    @Delete
    fun delete(data: MyFavorite): Completable

    @Delete
    fun delete(dataList: List<MyFavorite>): Completable

    @Query("DELETE FROM myFavorite WHERE folder=:name")
    fun delete(name: String)

    // https://stackoverflow.com/questions/48406228/room-select-query-with-in-condition
    @Query("DELETE FROM myFavorite WHERE folder IN(:name)")
    fun deleteByFolderNames(name: List<String>)

    @Query("DELETE FROM myFavorite WHERE _id=:index")
    fun deleteById(index: Int)
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
//    fun update(data: FrequentlySite): Completable

    @Delete
    fun delete(data: FrequentlySite): Completable

    @Query("DELETE FROM frequentlySite")
    fun deleteAll()
}
