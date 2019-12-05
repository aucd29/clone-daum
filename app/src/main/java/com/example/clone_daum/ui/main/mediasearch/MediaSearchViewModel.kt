package com.example.clone_daum.ui.main.mediasearch

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.clone_daum.common.Config
import brigitte.bindingadapter.AnimParams
import brigitte.viewmodel.CommandEventViewModel
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 16. <p/>
 */

class MediaSearchViewModel @Inject constructor(
    app: Application,
    val config: Config
) : CommandEventViewModel(app) {
    companion object {
        const val CMD_ANIM_FINISH    = "anim-finish"
        const val CMD_SEARCH_SPEECH  = "search-speech"
        const val CMD_SEARCH_MUSIC   = "search-music"
        const val CMD_SEARCH_FLOWER  = "search-flower"
        const val CMD_SEARCH_BARCODE = "search-barcode"
    }

    val containerTransY = ObservableField<AnimParams>()
    val dimmingBgAlpha  = ObservableField<AnimParams>()
    val overshootTransY = ObservableField<AnimParams>()
    val searchIconResId = ObservableInt(config.SEARCH_ICON)
}