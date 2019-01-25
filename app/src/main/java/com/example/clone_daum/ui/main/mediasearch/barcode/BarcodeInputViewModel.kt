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

class BarcodeInputViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), IFinishFragmentAware, ICommandEventAware
    , IDialogAware {

    companion object {
        private val mLog = LoggerFactory.getLogger(BarcodeInputViewModel::class.java)

        const val CMD_CLEAR_EDIT    = "clear-edt"
        const val CMD_HIDE_KEYBOARD = "hide-keyboard"
//        const val CMD_BARCODE       = "barcode"
    }

    override val dialogEvent  = SingleLiveEvent<DialogParam>()
    override val finishEvent  = SingleLiveEvent<Void>()
    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()

    val barcodeNumber = ObservableField<String>()
    val editorAction  = ObservableField<(String?) -> Boolean>()

    init {
        editorAction.set {
            if (mLog.isDebugEnabled) {
                mLog.debug("INSERTED BARCODE NUMBER : $it")
            }

            // 숫자를 입력 받아서 처리하는게 어떠한 의미를 가지는 줄 모르겠네 -_ -?
            // 도서 검색인가? 막상 도서 바코드 숫자 넣으면 안되고 -_ -?

            if (false) {
                // 먼가에 작업에 성공했으면 그 정보를 처리하면 될듯 싶은데 ?
                commandEvent(CMD_HIDE_KEYBOARD)
//                commandEvent(CMD_BARCODE, it!!)
            } else {
                alert(app, R.string.barcode_not_matched_info, R.string.barcode_not_detacted)
            }

            true
        }
    }

    override fun commandEvent(cmd: String) {
        when (cmd) {
            CMD_CLEAR_EDIT -> barcodeNumber.set("")
            else -> super.commandEvent(cmd)
        }
    }
}