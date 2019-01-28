package com.example.clone_daum.ui.search

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.model.remote.GithubService
import com.example.clone_daum.model.remote.HotSearchedWord
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
import java.util.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

class PopularViewModel @Inject constructor(app: Application)
    : RecyclerViewModel<PopularKeyword>(app), ICommandEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(PopularViewModel::class.java)

        const val CMD_BRS_SEARCH = "brs-search"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()

    private var mPopularList: HotSearchedWord? = null

    val dp                = CompositeDisposable()
    val visiblePopular    = ObservableInt(View.GONE)
    val chipLayoutManager = ObservableField<ChipsLayoutManager>()

    fun init() {
        // CHIP 레이아웃 의 아이템을 선택할 경우에 대해 처리 한다.
        initAdapter("search_recycler_popular_item")
        selectPopularList()
    }

    fun load(html: String) {
        if (mPopularList == null) {
            if (mLog.isDebugEnabled) {
                mLog.debug("HTML PARSE (POPUPLAR LIST)")
            }

            dp.add(Observable.just(html)
                .observeOn(Schedulers.io())
                .map(::parsePopular)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mPopularList = it
                })
        }
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

//<ul class="list_popular #search #search_hit #keyword">
//<li class="@0">
//<a href="https://m.search.daum.net/search?w=tot&amp;q=%EC%86%90%EC%84%9D%ED%9D%AC+%EB%AC%B8%EC%9E%90%EA%B3%B5%EA%B0%9C&amp;DA=NPI&amp;rtmaxcoll=NNS" class="link_suggest">손석희 문자공개</a>
//</li>
//<li class="@1">
//<a href="https://m.search.daum.net/search?w=tot&amp;q=%EB%B0%95%EC%A2%85%EC%B2%A0+%ED%8F%AD%ED%96%89+56%EC%96%B5&amp;DA=NPI&amp;rtmaxcoll=NNS" class="link_suggest">박종철 폭행 56억</a>
//</li>
//<li class="@2">
//<a href="https://m.search.daum.net/search?w=tot&amp;q=%EC%84%B8%EC%9D%B4%EC%85%B8%ED%95%AD%EA%B3%B5%EA%B6%8C&amp;DA=NPT" class="link_suggest">세이셸항공권</a>
//</li>
//<li class="@3"><a href="https://m.search.daum.net/search?w=tot&amp;q=%EC%B5%9C%EC%88%98%EC%A2%85+%EB%94%B8%EA%B3%B5%EA%B0%9C&amp;DA=NPI&amp;rtmaxcoll=NNS" class="link_suggest">최수종 딸공개</a>
//</li>
//<li class="@4"><a href="https://m.search.daum.net/search?w=tot&amp;q=%EC%BD%94%ED%81%90%ED%85%90&amp;DA=NPT" class="link_suggest">코큐텐 추천</a></li>
//</ul>

    private fun parsePopular(html: String): HotSearchedWord? {
        var data: HotSearchedWord? = null

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

        data = plaintext.jsonParse()

        return data
    }
}