package com.example.clone_daum.ui.search

import android.app.Application
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import com.example.clone_daum.R
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.SearchKeyword
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchViewModel @Inject constructor(
    app: Application,
    val db: DbRepository,
    val disposable: CompositeDisposable
) : RecyclerViewModel<SearchKeyword>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchViewModel::class.java)
    }

    val searchKeyword = ObservableField<String>()

    val searchEvent   = SingleLiveEvent<String>()
    val closeEvent    = SingleLiveEvent<Void>()
    val errorEvent    = SingleLiveEvent<String>()

    fun init() {
        initAdapter("search_recycler_history_item")
        items.set(db.searchHistoryDao.search().limit(4).blockingFirst())
    }

    fun reloadData() {
        disposable.add(db.searchHistoryDao.search().limit(4).subscribe {
            items.set(it)
        })
    }

    fun search(keyword: String?) {
        keyword?.let {
            disposable.add(db.searchHistoryDao.insert(SearchKeyword(
                keyword = it,
                date = System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (mLog.isDebugEnabled) {
                        mLog.debug("INSERTED DATA $it")
                    }

                    searchKeyword.set("")
                    reloadData()
                }, { e -> errorEvent(e.message) }
            ))

            searchEvent.value = it
        } ?: errorEvent(R.string.error_empty_keyword)
    }

    private fun errorEvent(@StringRes resid: Int) =
        errorEvent(string(resid))

    private fun errorEvent(msg: String?) {
        mLog.error("ERROR: $msg")

        msg?.run { errorEvent.value = this }
    }

    fun closeSearchHistory() {
        closeEvent.call()
    }

    fun deleteHistory(item: SearchKeyword) {
        disposable.add(db.searchHistoryDao.delete(item)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETED $item")
                }

                reloadData()
            }, { e -> errorEvent(e.message)}))
    }

    fun deleteAllHistory() {
        // delete query 는 왜? Completable 이 안되는가?
        ioThread {
            db.searchHistoryDao.deleteAll()
            reloadData()
        }
    }

    fun searchCancel() {
        closeEvent.call()
    }

    fun dateConvert(date: Long) = date.toDate("MM.dd.")
}