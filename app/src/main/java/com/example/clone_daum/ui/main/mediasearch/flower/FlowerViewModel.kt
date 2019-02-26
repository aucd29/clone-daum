package com.example.clone_daum.ui.main.mediasearch.flower

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
import com.example.common.arch.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 31. <p/>
 */

class FlowerViewModel @Inject constructor(app: Application)
    : AndroidViewModel(app), IFinishFragmentAware, ICommandEventAware {
    companion object {
        const val CMD_BRS_OPEN = "brs-open"
    }

    override val finishEvent  = SingleLiveEvent<Void>()
    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()

    val message = ObservableField<String>()
}