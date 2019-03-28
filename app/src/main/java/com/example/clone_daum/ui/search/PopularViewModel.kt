package com.example.clone_daum.ui.search

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.model.remote.PopularSearchedWord
import com.example.clone_daum.model.remote.PopularKeyword
import com.example.common.ICommandEventAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import com.example.common.jsonParse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

class PopularViewModel @Inject constructor(app: Application)
    : RecyclerViewModel<PopularKeyword>(app), ICommandEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(PopularViewModel::class.java)

        const val CMD_BRS_SEARCH = "brs-search"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()

    private var mPopularList: PopularSearchedWord? = null
    private lateinit var mDisposable: CompositeDisposable

    val visiblePopular    = ObservableInt(View.GONE)
    val chipLayoutManager = ObservableField<ChipsLayoutManager>()

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MAIN FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun load(html: String, disposable: CompositeDisposable) {
        mDisposable = disposable    // main fragment 의 composite disposable

        if (mPopularList == null) {
            if (mLog.isDebugEnabled) {
                mLog.debug("HTML PARSE (POPULAR LIST)")
            }

            mDisposable.add(Observable.just(html)
                .observeOn(Schedulers.io())
                .map(::parsePopular)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mPopularList = it
                })
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SEARCH FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun init() {
        // CHIP 레이아웃 의 아이템을 선택할 경우에 대해 처리 한다.
        initAdapter("search_recycler_popular_item")
        selectPopularList()
    }

    private fun selectPopularList() {
        mPopularList?.let {
            val pos = System.currentTimeMillis() % it.items.size
            val chooseItem = it.items.get(pos.toInt()).item
            if (mLog.isDebugEnabled) {
                mLog.debug("SELECTED LIST $pos, SIZE : ${chooseItem.size}")
                var keywords: String = ""
                chooseItem.forEach {
                    keywords += it.keyword + ", "
                }

                mLog.debug(keywords)
            }

            this.items.set(chooseItem)
            visiblePopular.set(View.VISIBLE)
        }
    }

    private fun parsePopular(html: String): PopularSearchedWord? {
        var data: PopularSearchedWord? = null

        val fk = "hotSearchedWordData = include("
        val ek = ");"

        val fp = html.indexOf(fk)
        if (fp == -1) {
            return null
        }

        val ep = html.indexOf(ek, fp)
        if (ep == -1) {
            return null
        }

        val plaintext = html.subSequence(fp + fk.length, ep - ek.length + 1).trim()
            .replace("(\n|\t)".toRegex(), "")

        try {
            data = plaintext.jsonParse()
        } catch (e: Exception) {
            if (mLog.isDebugEnabled) {
                e.printStackTrace()
            }

            mLog.error("ERROR: ${e.message}")

            return null
        }

        return data
    }
}