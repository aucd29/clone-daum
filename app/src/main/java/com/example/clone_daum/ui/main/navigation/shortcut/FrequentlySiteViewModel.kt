package com.example.clone_daum.ui.main.navigation.shortcut

import android.app.Application
import androidx.databinding.ObservableInt
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
            items.set(it)
        })
    }

    fun eventOpen(url: String) {
        if (url == FAVORITE_FRAGMENT) {
//            app.launchApp(url)
        } else {
            brsOpenEvent.value = url
        }
    }
}