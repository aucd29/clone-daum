package com.example.clone_daum.ui.main.navigation.shortcut

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.Sitemap
import brigitte.RecyclerViewModel
import brigitte.dpToPx
import com.example.clone_daum.R
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 2. <p/>
 */

class SitemapViewModel @Inject constructor(
    val preConfig: PreloadConfig,
    app: Application
) : RecyclerViewModel<Sitemap>(app) {

    companion object {
        const val CMD_OPEN_APP = "open-app"
    }

    val gridCount = ObservableInt(4)

    // 원래 대로라면 그냥 R값만 들어간 아이콘 형태는 아니고
    // 이미지 마스크 처리를 해야할 터인데
    // 귀차니즘... =_ = 으로 대체 [aucd29][2019-10-16]
    val roundedCorners = ObservableInt(10.dpToPx(app))

    init {
        items.set(preConfig.naviSitemapList)
    }
}