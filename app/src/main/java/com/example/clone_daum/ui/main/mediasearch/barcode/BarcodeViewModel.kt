package com.example.clone_daum.ui.main.mediasearch.barcode

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
import com.example.common.arch.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 23. <p/>
 */

class BarcodeViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), IFinishFragmentAware, ICommandEventAware {
    companion object {
        const val CMD_FILE_OPEN  = "file-open"
        const val CMD_INPUT_CODE = "input-code"
    }

    override val finishEvent  = SingleLiveEvent<Void>()
    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()
}