package com.example.clone_daum.ui.search

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.model.remote.PopularSearchedWord
import com.example.clone_daum.model.remote.PopularKeyword
import brigitte.RecyclerViewModel
import brigitte.jsonParse
import com.example.clone_daum.R
import com.example.clone_daum.common.PreloadConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

class PopularViewModel @Inject constructor(
    val disposable: CompositeDisposable,
    layoutManager: Provider<ChipsLayoutManager>,
    app: Application
) : RecyclerViewModel<PopularKeyword>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PopularViewModel::class.java)

        const val CMD_BRS_SEARCH = "brs-search"
    }

    private var mPopularList: PopularSearchedWord? = null

    val visiblePopular    = ObservableInt(View.GONE)
    val chipLayoutManager = ObservableField(layoutManager.get())

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MAIN FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun load(html: String) {
        if (mPopularList == null) {
            if (mLog.isDebugEnabled) {
                mLog.debug("HTML PARSE (POPULAR LIST)")
            }

            disposable.add(Observable.just(html)
                .subscribeOn(Schedulers.io())
                .map(::parsePopular)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mPopularList = it
                }, ::errorLog))
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SEARCH FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun init() {
        // CHIP 레이아웃 의 아이템을 선택할 경우에 대해 처리 한다.
        initAdapter(R.layout.search_recycler_popular_item)
        selectPopularList()
    }

    private fun selectPopularList() {
        mPopularList?.let {
            val pos = System.currentTimeMillis() % it.items.size
            val chooseItem = it.items[pos.toInt()].item
            if (mLog.isDebugEnabled) {
                mLog.debug("SELECTED LIST $pos, SIZE : ${chooseItem.size}")
                var keywords = ""
                chooseItem.forEach { f ->
                    keywords += f.keyword + ", "
                }

                mLog.debug(keywords)
            }

            this.items.set(chooseItem)
            visiblePopular.set(View.VISIBLE)
        }
    }

    // 이건 좀 에반대  [aucd29][2019-08-22]
//    {
//        "items": [
//        {
//            "item": [
//            {
//            "keyword": "핸드폰거치대",
//            "url": "https://search.daum.net/search?w=tot&q=%ED%95%B8%EB%93%9C%ED%8F%B0%EA%B1%B0%EC%B9%98%EB%8C%80&DA=NPT"
//            }]
//        },
//        {
//            "item": [{
//            {
//            "keyword": "알로에겔",
//            "url": "https://search.daum.net/search?w=tot&q=%EC%95%8C%EB%A1%9C%EC%97%90%EA%B2%94&DA=NPT"
//        }]
//        },

    // [ [ {},{} ] ] 면 될거를 { "items": [ "item": [ {}, {}] ] } 이러고 있으니.. =_ = 내 참..
    // 이런 애들은 뽑고 나는.. 젠장 ㅋㅋㅋㅋ 머 js 에서 이렇게 달라고 한걸로 대충 이해하자

    private fun parsePopular(html: String): PopularSearchedWord? {
        var data: PopularSearchedWord? = null

        val fk = "hotSearchedWordData = include("
        val ek = ");"

        val f = html.indexOf(fk) + fk.length
        val e = html.indexOf(ek, f)

        if (f == fk.length || e == -1) {
            mLog.error("ERROR: INVALID HTML DATA")

            return data
        }

        val plaintext = html.substring(f, e)
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