package com.example.clone_daum.ui.search

import android.app.Application
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.R
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.SearchHistory
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.ISearchRecyclerData
import com.example.clone_daum.model.remote.SuggestItem
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchViewModel @Inject constructor(app: Application)
    : RecyclerViewModel<ISearchRecyclerData>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchViewModel::class.java)

        const val RECENT_SEARCH_LIMIT = 4L
        const val K_RECENT_SEARCH     = "search-recent-search"
    }

    @Inject lateinit var daum: DaumService
    @Inject lateinit var db: DbRepository
    @Inject lateinit var disposable: CompositeDisposable

    val searchKeyword               = ObservableField<String>()
    var showSearchRecyclerLayout    = prefs().getBoolean(K_RECENT_SEARCH, true)
    val toggleRecentSearchText      = ObservableInt()
    val toggleEmptyAreaText         = ObservableInt()
    val editorAction                = ObservableField<(String?) -> Boolean>()

    val visibleSearchRecycler       = ObservableInt(View.VISIBLE)
    val visibleSearchEmpty          = ObservableInt(View.GONE)
    val visibleBottomButtons        = ObservableInt(View.VISIBLE)

    val searchEvent                 = SingleLiveEvent<String>()
    val closeEvent                  = SingleLiveEvent<Void>()
    val dlgEvent                    = SingleLiveEvent<DialogParam>()
    val errorEvent                  = SingleLiveEvent<String>()

    fun init() {
        editorAction.set {
            eventSearch(it)

            true
        }

        initAdapter(arrayOf("search_recycler_history_item", "search_recycler_suggest_item"))

        val searchList = db.searchHistoryDao.search().limit(RECENT_SEARCH_LIMIT).blockingFirst()
        items.set(searchList)
        visibleSearchRecycler(searchList.size > 0)
    }

    fun reloadHistoryData() {
        disposable.add(db.searchHistoryDao.search().limit(RECENT_SEARCH_LIMIT)
            .subscribe {
                items.set(it)

                visibleSearchRecycler(it.size > 0)
            })
    }

    fun eventSearch(keyword: String?) {
        keyword?.let {
            disposable.add(db.searchHistoryDao.insert(SearchHistory(
                keyword = it,
                date = System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (mLog.isDebugEnabled) {
                        mLog.debug("INSERTED DATA $it")
                    }

                    searchKeyword.set("")
                    reloadHistoryData()
                }, { e -> errorEvent(e.message) }
            ))

            searchEvent.value = it
        } ?: errorEvent(R.string.error_empty_keyword)
    }

    fun eventToggleRecentSearch() {
        if (showSearchRecyclerLayout) {
            dlgEvent.value = DialogParam(
                title    = string(R.string.dlg_title_stop_using_recent_search),
                message  = string(R.string.dlg_msg_do_you_want_stop_using_recent_search),
                listener = { res, _ -> if (res) toggleSearchRecyclerLayout() },
                positiveStr = string(android.R.string.ok),
                negativeStr = string(android.R.string.cancel)
            )
        } else {
            toggleSearchRecyclerLayout()
        }
    }

    private fun toggleSearchRecyclerLayout() {
        showSearchRecyclerLayout = !showSearchRecyclerLayout

        prefs().edit { putBoolean(K_RECENT_SEARCH, showSearchRecyclerLayout) }

        visibleSearchRecycler(items.get()!!.size > 0)
    }

    private fun visibleSearchRecycler(res: Boolean) {
        if (mLog.isDebugEnabled) {
            mLog.debug("EXIST LIST $res, SHOW LAYOUT $showSearchRecyclerLayout")
        }

        if (res && showSearchRecyclerLayout) {
            visibleSearchRecycler.set(View.VISIBLE)
            visibleSearchEmpty.set(View.GONE)
        } else {
            visibleSearchRecycler.set(View.GONE)
            visibleSearchEmpty.set(View.VISIBLE)
        }

        if (showSearchRecyclerLayout) {
            toggleRecentSearchText.set(R.string.search_turn_off_recent_search)
            toggleEmptyAreaText.set(R.string.search_empty_recent_search)
        } else {
            toggleRecentSearchText.set(R.string.search_turn_on_recent_search)
            toggleEmptyAreaText.set(R.string.search_off_recent_search)
        }
    }

    fun eventDeleteHistory(item: SearchHistory) {
        disposable.add(db.searchHistoryDao.delete(item)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETED : $item")
                }

                reloadHistoryData()
            }, { e -> errorEvent(e.message) }))
    }

    fun eventDeleteAllHistory() {
        dlgEvent.value = DialogParam(
            title    = string(R.string.dlg_title_delete_all_searched_list),
            message  = string(R.string.dlg_msg_delete_all_searched_list),
            listener = { res, _ ->
                if (res) {
                    // delete query 는 왜? Completable 이 안되는가?
                    ioThread {
                        db.searchHistoryDao.deleteAll()
                        reloadHistoryData()
                    }
                }
            },
            positiveStr = string(android.R.string.ok),
            negativeStr = string(android.R.string.cancel)
        )
    }

    fun eventCloseSearchFragment() {
        if (mLog.isDebugEnabled) {
            mLog.debug("FINISH SEARCH FRAGMENT")
        }

        searchKeyword.set("")
        closeEvent.call()
    }

    fun dateConvert(date: Long) = date.toDate("MM.dd.")

    fun suggest(keyword: String) {
        disposable.add(daum.suggest(keyword)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                mLog.debug("QUERY : ${it.q}, SIZE: ${it.subkeys.size}")

                val suggestList: ArrayList<SuggestItem> = arrayListOf()
                it.subkeys.forEach { key ->
                    val newkey = key.replace(it.q, "<font color='#ff7b39'><b>${it.q}</b></font>")
                    suggestList.add(SuggestItem(newkey))
                }

                items.set(suggestList.toList())
            }, { e -> errorEvent(e.message) }))
    }

    fun eventSearchSuggest(keyword: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("EVENT SEARCH SUGGEST : $keyword")
        }
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("TEXT CHANGED $s ($count)")
        }

        visibleBottomButtons.set(if (count > 0) {
            suggest(s.toString())

            visibleSearchRecycler.set(View.VISIBLE)
            visibleSearchEmpty.set(View.GONE)

            View.GONE
        } else {
            reloadHistoryData()
            View.VISIBLE
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun errorEvent(@StringRes resid: Int) =
        errorEvent(string(resid))

    private fun errorEvent(msg: String?) {
        mLog.error("ERROR: $msg")

        msg?.run { errorEvent.value = this }
    }
}