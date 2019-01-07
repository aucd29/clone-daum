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
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 2. <p/>
 */

class FrequentlySiteViewModel @Inject constructor(app: Application
    , val disposable: CompositeDisposable
    , val frequentlySiteDao: FrequentlySiteDao)
    : RecyclerViewModel<FrequentlySite>(app) {
    companion object {
        const val FAVORITE_FRAGMENT = "favListFragment"
    }

    val gridCount    = ObservableInt(5)
    val brsOpenEvent = SingleLiveEvent<String>()

    init {
        initAdapter("frequently_item")

        disposable.add(frequentlySiteDao.select().subscribe {
            (it as ArrayList<FrequentlySite>).add(FrequentlySite(
                title = "사이트이동", url = "http://daum.net", count = 1))
            items.set(it)
        })
    }

    fun eventIconText(url: String) =
        url.replace("^(http|https)://".toRegex(), "")
            .substring(0, 1)
            .toUpperCase()

    fun eventIconBackground(url: String) =
        when (eventIconText(url).toCharArray().get(0).toInt() % 2) {
            0    -> R.drawable.shape_frequently_0_background
            else -> R.drawable.shape_frequently_1_background
        }

    fun eventOpen(url: String) {
        brsOpenEvent.value = url
    }
}