package com.example.clone_daum.ui.search

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.model.remote.PopularSearchedWord
import com.example.clone_daum.model.remote.PopularKeyword
import brigitte.RecyclerViewModel2
import brigitte.di.dagger.module.RxSchedulers
import brigitte.jsonParse
import com.example.clone_daum.R
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
    private val dp: CompositeDisposable,
    private val layoutManager: Provider<ChipsLayoutManager>,
    private val schedulers: RxSchedulers,
    app: Application
) : RecyclerViewModel2<PopularKeyword>(app) {
    companion object {
        private val logger = LoggerFactory.getLogger(PopularViewModel::class.java)

        const val CMD_BRS_SEARCH = "brs-search"
    }

    private var popularList: PopularSearchedWord? = null

    val viewPopular       = ObservableInt(View.GONE)
    val chipLayoutManager = ObservableField<ChipsLayoutManager>()

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MAIN FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun load(html: String) {
        if (popularList == null) {
            if (logger.isDebugEnabled) {
                logger.debug("HTML PARSE (POPULAR LIST)")
            }

            dp.add(Observable.just(html)
                .subscribeOn(schedulers.io())
                .map(::parsePopular)
                .observeOn(schedulers.ui())
                .subscribe({
                    popularList = it
                }, ::errorLog))
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SEARCH FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun init() {
        chipLayoutManager.set(layoutManager.get())
        selectPopularList()
    }

    private fun selectPopularList() {
        popularList?.let {
            val pos = System.currentTimeMillis() % it.items.size
            val chooseItem = it.items[pos.toInt()].item
            if (logger.isDebugEnabled) {
                logger.debug("SELECTED LIST $pos, SIZE : ${chooseItem.size}")
                var keywords = ""
                chooseItem.forEach { f ->
                    keywords += f.keyword + ", "
                }

                logger.debug(keywords)
            }

            this.items.set(chooseItem)
            viewPopular.set(View.VISIBLE)
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

    // [ [ {},{} ] ] 면 될거를 { "items": [ "item": [ {}, {}] ] } 이렇게 하는건 =_ =?

    private fun parsePopular(html: String): PopularSearchedWord? {
        var data: PopularSearchedWord? = null

        val fk = "hotSearchedWordData = include("
        val ek = ");"

        val f = html.indexOf(fk) + fk.length
        val e = html.indexOf(ek, f)

        if (f == fk.length || e == -1) {
            logger.error("ERROR: INVALID HTML DATA")

            return data
        }

        val plaintext = html.substring(f, e)
            .replace("(\n|\t)".toRegex(), "")

        try {
            data = plaintext.jsonParse()
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                e.printStackTrace()
            }

            logger.error("ERROR: ${e.message}")

            return null
        }

        return data
    }
}