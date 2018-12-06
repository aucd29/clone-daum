package com.example.clone_daum.ui.search

import android.app.Application
import android.databinding.ObservableField
import com.example.clone_daum.model.Repository
import com.example.clone_daum.model.local.SearchKeyword
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import com.example.common.toDate

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchViewModel(app: Application) : RecyclerViewModel<SearchKeyword>(app) {
    val searchKeyword = ObservableField<String>()

    val searchEvent = SingleLiveEvent<String>()
    val closeEvent = SingleLiveEvent<Void>()

//    init {
//        val d = Repository.searchHistoryDao.search().subscribe {
//            initAdapter("search_recycler_history_item")
//            setItems(it)
//        }
//    }

    fun search(keyword: String) {
//        ioThread {
//            Repository.searchHistoryDao.insert(
//                SearchKeyword(keyword = keyword, date = System.currentTimeMillis()))
//        }

        searchEvent.value = searchKeyword.get()
    }

    fun closeSearchHistory() {
        closeEvent.call()
    }

    fun deleteHistory(item: SearchKeyword) {
        Repository.searchHistoryDao.delete(item)
    }

    fun deleteAllHistory() {
        Repository.searchHistoryDao.deleteAll()
        setItems(listOf())
    }

    fun searchCancel() {
//        val df = SimpleDateFormat("yyyyMMdd  HH:mm")

    }

    fun dateConvert(date: Long) = date.toDate("MM.dd.")
}