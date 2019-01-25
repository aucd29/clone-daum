package com.example.clone_daum.ui.main.mediasearch.barcode

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 23. <p/>
 */

class BarcodeViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), IFinishFragmentAware, ICommandEventAware {

    // REFERENCE
    // https://zxing.github.io/zxing/
    // https://www.tec-it.com/download/PDF/Barcode_Reference_EN.pdf
    // https://developers.google.com/vision/android/barcodes-overview
    // https://elwlsek.tistory.com/453

    // TEST
    // http://www.barcode-generator.org/

    companion object {
        private val mLog = LoggerFactory.getLogger(BarcodeViewModel::class.java)

        const val CMD_FILE_OPEN  = "file-open"
        const val CMD_INPUT_CODE = "input-code"
    }

    override val finishEvent  = SingleLiveEvent<Void>()
    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()
}