package com.example.clone_daum.ui.search

import android.app.Application
import androidx.databinding.ObservableField
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.SearchKeyword
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import com.example.common.toDate
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchViewModel @Inject constructor(
    app: Application,
    val db: DbRepository,
    val disposable: CompositeDisposable
) : RecyclerViewModel<SearchKeyword>(app) {
    val searchKeyword = ObservableField<String>()

    val searchEvent   = SingleLiveEvent<String>()
    val closeEvent    = SingleLiveEvent<Void>()

    init {
        disposable.add(db.searchHistoryDao.search().subscribe {
            initAdapter("search_recycler_history_item")
            setItems(it)
        })
    }

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
        db.searchHistoryDao.delete(item)
    }

    fun deleteAllHistory() {
        db.searchHistoryDao.deleteAll()
        setItems(listOf())
    }

    fun searchCancel() {
//        val df = SimpleDateFormat("yyyyMMdd  HH:mm")

    }

    fun dateConvert(date: Long) = date.toDate("MM.dd.")
}