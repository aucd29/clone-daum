package com.example.clone_daum.model

import android.content.Context
import com.example.clone_daum.model.local.SearchHistoryDao
import com.example.clone_daum.model.local.LocalDatabase
import com.example.clone_daum.model.local.PopularKeywordDao
import io.reactivex.Observable

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object DataManager {
    lateinit var searchHistoryDao: SearchHistoryDao
    lateinit var popularKeywordDao: PopularKeywordDao

    fun init(context: Context) {
        LocalDatabase.get(context).run {
            searchHistoryDao  = searchHistoryDao()
            popularKeywordDao = popularKeywordDao()
        }
    }

    fun tabList(context: Context) =
        Observable.just(context.assets.open("res/tab.json").readBytes())

//    fun search() = searchHistoryDao.search()
//    fun insert(keyword: SearchKeyword) = searchHistoryDao.insert(keyword)
//    fun update(keyword: SearchKeyword) = searchHistoryDao.update(keyword)
//    fun delete(keyword: SearchKeyword) = searchHistoryDao.delete(keyword)
}