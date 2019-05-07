package com.example.clone_daum.ui.main.mediasearch.music

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import brigitte.CommandEventViewModel
import brigitte.ICommandEventAware
import brigitte.arch.SingleLiveEvent
import brigitte.bindingadapter.AnimParams
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class MusicViewModel @Inject constructor(app: Application)
    : CommandEventViewModel(app) {

    val bgScale = ObservableField<AnimParams>()
}