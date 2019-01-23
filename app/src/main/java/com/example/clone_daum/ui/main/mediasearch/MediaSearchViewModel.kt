package com.example.clone_daum.ui.main.mediasearch

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 16. <p/>
 */

class MediaSearchViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), ICommandEventAware, IFinishFragmentAware {
    companion object {
        const val CMD_ANIM_FINISH    = "anim-finish"
        const val CMD_SEARCH_SPEECH  = "search-speech"
        const val CMD_SEARCH_MUSIC   = "search-music"
        const val CMD_SEARCH_FLOWER  = "search-flower"
        const val CMD_SEARCH_BARCODE = "search-barcode"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()
    override val finishEvent  = SingleLiveEvent<Void>()

    val containerTransY = ObservableField<AnimParams>()
    val dimmingBgAlpha  = ObservableField<AnimParams>()
    val overshootTransY = ObservableField<AnimParams>()
    val bgScale         = ObservableField<AnimParams>()

    val speechMessageResId = ObservableField<Int>(R.string.voice_pls_speak_search_keyword)
}