package com.example.clone_daum.ui.main.mediasearch.flower

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import brigitte.ICommandEventAware
import brigitte.arch.SingleLiveEvent
import brigitte.viewmodel.CommandEventViewModel
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 31. <p/>
 */

class FlowerViewModel @Inject constructor(app: Application)
    : CommandEventViewModel(app) {
    companion object {
        const val CMD_BRS_OPEN = "brs-open"
    }

    val message = ObservableField<String>()
}