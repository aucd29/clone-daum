package com.example.clone_daum.ui.main.mediasearch.music

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.common.IFinishFragmentAware
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class MusicViewModel @Inject constructor(app: Application)
    : AndroidViewModel(app), IFinishFragmentAware {

    override val finishEvent  = SingleLiveEvent<Void>()

    val bgScale = ObservableField<AnimParams>()
}