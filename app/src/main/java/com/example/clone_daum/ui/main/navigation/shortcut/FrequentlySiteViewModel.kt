package com.example.clone_daum.ui.main.navigation.shortcut

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.FrequentlySiteDao
import brigitte.RecyclerViewModel2
import brigitte.arch.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 2. <p/>
 */

class FrequentlySiteViewModel @Inject constructor(
    private val frequentlySiteDao: FrequentlySiteDao,
    app: Application
) : RecyclerViewModel2<FrequentlySite>(app) {
    companion object {
        const val CMD_BROWSER = "browser"
    }

    private val dp = CompositeDisposable()

    val gridCount    = ObservableInt(4)
    val brsOpenEvent = SingleLiveEvent<String>()

    fun init() {
        dp.add(frequentlySiteDao.select()
            .subscribe {
                items.set(it)
            })
    }

    fun eventIconText(item: FrequentlySite) =
        item.url.replace("^(http|https)://".toRegex(), "")
            .substring(0, 1)
            .toUpperCase()

    override fun onCleared() {
        dp.dispose()

        super.onCleared()
    }
}