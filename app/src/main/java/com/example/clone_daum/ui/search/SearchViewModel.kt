package com.example.clone_daum.ui.search

import android.app.Application
import androidx.databinding.ObservableField
import com.example.clone_daum.model.DataManager
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
//        val d = DataManager.searchHistoryDao.search().subscribe {
//            initAdapter("search_recycler_history_item")
//            setItems(it)
//        }
//    }

    fun search(keyword: String) {
//        ioThread {
//            DataManager.searchHistoryDao.insert(
//                SearchKeyword(keyword = keyword, date = System.currentTimeMillis()))
//        }

        searchEvent.value = searchKeyword.get()
    }

    fun closeSearchHistory() {
        closeEvent.call()
    }

    fun deleteHistory(item: SearchKeyword) {
        DataManager.searchHistoryDao.delete(item)
    }

    fun deleteAllHistory() {
        DataManager.searchHistoryDao.deleteAll()
        setItems(listOf())
    }

    fun searchCancel() {
//        val df = SimpleDateFormat("yyyyMMdd  HH:mm")

    }

    fun dateConvert(date: Long) = date.toDate("MM.dd.")
}