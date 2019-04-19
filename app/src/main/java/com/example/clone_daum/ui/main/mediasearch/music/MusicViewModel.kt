package com.example.clone_daum.ui.main.mediasearch.music

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.common.CommandEventViewModel
import com.example.common.ICommandEventAware
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class MusicViewModel @Inject constructor(app: Application)
    : CommandEventViewModel(app) {

    val bgScale = ObservableField<AnimParams>()
}