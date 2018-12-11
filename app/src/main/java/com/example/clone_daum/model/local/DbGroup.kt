package com.example.clone_daum.model.local

import androidx.room.*
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

@Entity(tableName = "searchHistory")
data class SearchKeyword (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val keyword: String,
    val date: Long
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff)= this._id == (item as SearchKeyword)._id
}

@Entity(tableName = "popularKeyword")
data class PopularKeyword (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val keyword: String
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff)= this._id == (item as PopularKeyword)._id
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM searchHistory ORDER BY _id DESC")
    fun search(): Flowable<List<SearchKeyword>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keyword: SearchKeyword): Completable

    @Update
    fun update(keyword: SearchKeyword): Completable

    @Delete
    fun delete(keyword: SearchKeyword): Completable

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

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Database(entities = [
    SearchKeyword::class,
    PopularKeyword::class
], version = 1)
abstract class LocalDb: RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun popularKeywordDao(): PopularKeywordDao
}