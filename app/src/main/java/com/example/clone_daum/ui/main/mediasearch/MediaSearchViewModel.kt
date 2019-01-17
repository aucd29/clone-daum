package com.example.clone_daum.ui.main.mediasearch

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.common.ICommandEventAware
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 16. <p/>
 */

class MediaSearchViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), ICommandEventAware {

    companion object {
        const val CMD_ANIM_FINISH = "anim-finish"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()

    val containerTransY = ObservableField<AnimParams>()
    val dimmingBgAlpha  = ObservableField<AnimParams>()
    val bounceTransY    = ObservableField<AnimParams>()
}