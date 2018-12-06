package com.example.clone_daum.model.local

import android.content.Context
import androidx.room.*
import com.example.common.IRecyclerDiff
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

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM searchHistory")
    fun search(): Flowable<List<SearchKeyword>>

    @Insert
    fun insert(keyword: SearchKeyword)

    @Update
    fun update(keyword: SearchKeyword)

    @Delete
    fun delete(keyword: SearchKeyword)

    @Query("DELETE FROM searchHistory")
    fun deleteAll()
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Entity(tableName = "popularKeyword")
data class PopularKeyword (
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val keyword: String
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff)= this._id == (item as PopularKeyword)._id
}

@Dao
interface PopularKeywordDao {
    @Query("SELECT * FROM popularKeyword")
    fun searchPopularKeywordList(): List<PopularKeyword>

    @Insert
    fun insertPopularKeyword(keyword: PopularKeyword)

    @Update
    fun updatePopularKeyword(keyword: PopularKeyword)

    @Delete
    fun deletePopularKeyword(keyword: PopularKeyword)
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Database(entities = arrayOf(SearchKeyword::class, PopularKeyword::class), version = 1)
abstract class LocalRepository: RoomDatabase() {
    companion object {
        private var db: LocalRepository? = null

        fun get(context: Context): LocalRepository {
            if (db == null) {
                synchronized(this) {
                    db = Room.databaseBuilder(context, LocalRepository::class.java, "local.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return db!!
        }


        fun destroy() {
            db = null
        }
    }

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun popularKeywordDao(): PopularKeywordDao
}