package com.example.clone_daum.ui.main.navigation.shortcut

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.Sitemap
import com.example.common.RecyclerViewModel
import com.example.common.app
import com.example.common.arch.SingleLiveEvent
import com.example.common.launchApp
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 2. <p/>
 */

class SitemapViewModel @Inject constructor(app: Application
    , val preConfig: PreloadConfig)
    : RecyclerViewModel<Sitemap>(app) {

    val gridCount    = ObservableInt(5)
    val brsOpenEvent = SingleLiveEvent<String>()

    init {
        initAdapter("sitemap_item")
        items.set(preConfig.naviSitemapList)
    }

    fun eventOpen(item: Sitemap) {
        if (item.isApp) {
            app.launchApp(item.url)
        } else {
            brsOpenEvent.value = item.url
        }
    }
}