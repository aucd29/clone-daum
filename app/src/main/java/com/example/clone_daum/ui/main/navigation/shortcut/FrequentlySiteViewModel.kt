package com.example.clone_daum.ui.main.navigation.shortcut

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.FrequentlySiteDao
import com.example.common.RecyclerViewModel
import com.example.common.app
import com.example.common.arch.SingleLiveEvent
import com.example.common.launchApp
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 2. <p/>
 */

class FrequentlySiteViewModel @Inject constructor(app: Application
    , val disposable: CompositeDisposable
    , val frequentlySiteDao: FrequentlySiteDao)
    : RecyclerViewModel<FrequentlySite>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(FrequentlySiteViewModel::class.java)

        const val DEFAULT_TITLE = "사이트이동"
    }

    val gridCount    = ObservableInt(5)
    val brsOpenEvent = SingleLiveEvent<String>()

    init {
        initAdapter("frequently_item")

        disposable.add(frequentlySiteDao.select().subscribe {
            // 마지막 아이템에 기본 값을 추가 함
            (it as ArrayList<FrequentlySite>).add(FrequentlySite(
                title = DEFAULT_TITLE, url = "http://m.daum.net", count = 1))

            items.set(it)
        })
    }

    fun eventIconText(item: FrequentlySite) =
        if (item.title == DEFAULT_TITLE) {
            "http"
        } else {
            item.url.replace("^(http|https)://".toRegex(), "")
                .substring(0, 1)
                .toUpperCase()
        }

    fun eventOpen(url: String) {
        brsOpenEvent.value = url
    }
}