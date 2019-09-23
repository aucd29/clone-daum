package com.example.clone_daum.ui.search

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.RecyclerView
import com.example.clone_daum.R
import com.example.clone_daum.common.Config
import com.example.clone_daum.model.local.SearchHistory
import com.example.clone_daum.model.remote.DaumSuggestService
import com.example.clone_daum.model.ISearchRecyclerData
import com.example.clone_daum.model.local.SearchHistoryDao
import com.example.clone_daum.model.remote.SuggestItem
import brigitte.*
import brigitte.arch.SingleLiveEvent
import brigitte.viewmodel.app
import com.example.clone_daum.di.module.ViewModelAssistedFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
//import com.example.clone_daum.di.module.ViewModelAssistedFactory
//import com.squareup.inject.assisted.Assisted
//import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 29. <p/>
 *
 * https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle
 */

class SearchViewModel @AssistedInject constructor(
    @Assisted private val stateHandle: SavedStateHandle,
    private val config: Config,
    private val daum: DaumSuggestService,
    private val searchDao: SearchHistoryDao,
    private val mDisposable: CompositeDisposable,
    app: Application
) : RecyclerViewModel<ISearchRecyclerData>(app), IDialogAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchViewModel::class.java)

        const val K_RECENT_SEARCH = "search-recent-search"

        const val CMD_BRS_OPEN    = "brs-open"
        const val CMD_BRS_SEARCH  = "brs-search"

        const val STS_KEYWORD     = "keyword"
    }

    override val dialogEvent = SingleLiveEvent<DialogParam>()

    val searchKeyword            = ObservableField<String>(stateHandle.getLiveData<String>(STS_KEYWORD, "").value)
    val searchIconResId          = ObservableInt(config.SEARCH_ICON)
    val toggleRecentSearchText   = ObservableInt(R.string.search_turn_off_recent_search)
    val toggleEmptyAreaText      = ObservableInt(R.string.search_empty_recent_search)
    val editorAction             = ObservableField<(String?) -> Boolean>()

    var prefSearchRecycler       = prefs().getBoolean(K_RECENT_SEARCH, true)
    val visibleSearchRecycler    = ObservableInt(View.VISIBLE)
    val visibleBottomButtons     = ObservableInt(View.VISIBLE)

    // https://stackoverflow.com/questions/29873859/how-to-implement-itemanimator-of-recyclerview-to-disable-the-animation-of-notify/30837162
    val itemAnimator             = ObservableField<RecyclerView.ItemAnimator?>()


    fun init() {
        editorAction.set {
            eventSearch(it)
            true
        }

        initAdapter(R.layout.search_recycler_history_item, R.layout.search_recycler_suggest_item)
        reloadHistoryData()
    }

    fun reloadHistoryData() {
        mDisposable.add(searchDao.search()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                items.set(it)
                visibleSearchRecycler(it.isNotEmpty())
            }, ::errorLog))
    }

    fun eventSearch(keyword: String?) {
        keyword?.let {
            mDisposable.add(searchDao.insert(SearchHistory(
                keyword = it,
                date    = System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (mLog.isDebugEnabled) {
                        mLog.debug("INSERTED DATA $it")
                    }

                    searchKeyword.set("")
                }, { e ->
                    errorLog(e)
                    snackbar(e)
                }))

            command(CMD_BRS_SEARCH, it)
        } ?: snackbar(R.string.error_empty_keyword)
    }

    fun eventToggleRecentSearch() {
        if (prefSearchRecycler) {
            confirm(app, R.string.dlg_msg_do_you_want_stop_using_recent_search, R.string.dlg_title_stop_using_recent_search, listener = { res, _ ->
                    if (res) toggleSearchRecyclerLayout()
                })
        } else {
            toggleSearchRecyclerLayout()
        }
    }

    private fun toggleSearchRecyclerLayout() {
        prefSearchRecycler = !prefSearchRecycler

        prefs().edit { putBoolean(K_RECENT_SEARCH, prefSearchRecycler) }

        visibleSearchRecycler(items.get()!!.isNotEmpty())
    }

    private fun visibleSearchRecycler(res: Boolean) {
        if (mLog.isDebugEnabled) {
            mLog.debug("EXIST LIST $res, SHOW LAYOUT $prefSearchRecycler")
        }

        visibleSearchRecycler.set(if (res && prefSearchRecycler) {
            View.VISIBLE
        } else {
            View.GONE
        })

        if (prefSearchRecycler) {
            toggleRecentSearchText.set(R.string.search_turn_off_recent_search)
            toggleEmptyAreaText.set(R.string.search_empty_recent_search)
        } else {
            toggleRecentSearchText.set(R.string.search_turn_on_recent_search)
            toggleEmptyAreaText.set(R.string.search_off_recent_search)
        }
    }

    fun eventDeleteHistory(item: SearchHistory) {
        mDisposable.add(searchDao.delete(item)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETED : $item")
                }
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun eventDeleteAllHistory() {
        confirm(app, R.string.dlg_msg_delete_all_searched_list, R.string.dlg_title_delete_all_searched_list
            , listener = { res, _ -> if (res) {
                mDisposable.add(Completable.fromAction { searchDao.deleteAll() }
                    .subscribeOn(Schedulers.io())
                    .subscribe ({
                        if (mLog.isDebugEnabled) {
                            mLog.debug("DELETE ALL")
                        }
                    }, ::errorLog))
            }})
    }

    fun dateConvert(date: Long) = date.toDate("MM.dd.")

    fun suggest(keyword: String) {
//        stateHandle.set(STS_KEYWORD, keyword)

        mDisposable.add(daum.suggest(keyword)
            .map {
                mLog.debug("QUERY : ${it.q}, SIZE: ${it.subkeys.size}")

                val suggestList: ArrayList<SuggestItem> = arrayListOf()
                it.subkeys.forEach { key ->
                    // 좀더 빠르게 하려고 client 의 replace 가 아닌 서버의 highlighted 값을 참조 하는듯
                    // 일단 귀차니즘으로 replace =_ = 훗...
                    // 생각해보니 초성만 입력했을때 하이라이트 시키려면 문제가 되긴하는구나..
                    val newkey = key.replace(it.q,
                        "<font color='#ff7b39'><b>${it.q}</b></font>")
                    suggestList.add(SuggestItem(newkey, key))
                }

                suggestList
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                items.set(it.toList())
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("TEXT CHANGED $s ($count)")
        }

        visibleBottomButtons.set(if (count > 0) {
            suggest(s.toString())
            visibleSearchRecycler.set(View.VISIBLE)

            View.GONE
        } else {
            reloadHistoryData()
            View.VISIBLE
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FACTORY
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<SearchViewModel>
}