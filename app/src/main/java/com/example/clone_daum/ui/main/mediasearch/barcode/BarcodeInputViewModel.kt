package com.example.clone_daum.ui.main.mediasearch.barcode

import android.app.Application
import androidx.databinding.ObservableField
import com.example.clone_daum.R
import brigitte.*
import brigitte.arch.SingleLiveEvent
import brigitte.viewmodel.CommandEventViewModel
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 23. <p/>
 */

class BarcodeInputViewModel @Inject constructor(
    app: Application
) : CommandEventViewModel(app), IDialogAware {
    companion object {
        private val logger = LoggerFactory.getLogger(BarcodeInputViewModel::class.java)

        const val CMD_CLEAR_EDIT    = "clear-edt"
        const val CMD_HIDE_KEYBOARD = "hide-keyboard"
    }

    override val dialogEvent  = SingleLiveEvent<DialogParam>()

    val barcodeNumber = ObservableField<String>()
    val editorAction  = ObservableField<(String?) -> Boolean>()

    init {
        editorAction.set {
            if (logger.isDebugEnabled) {
                logger.debug("INSERTED BARCODE NUMBER : $it")
            }

            // 숫자를 입력 받아서 처리하는게 어떠한 의미를 가지는 줄 모르겠네 -_ -?
            // 도서 검색인가? 막상 도서 바코드 숫자 넣으면 안되고 -_ -?

            if (false) {
                // 먼가에 작업에 성공했으면 그 정보를 처리하면 될듯 싶은데 ?
                command(CMD_HIDE_KEYBOARD)
//                command(CMD_BARCODE, it!!)
            } else {
                alert(this.app, R.string.barcode_not_matched_info, R.string.barcode_not_detacted)
            }

            true
        }
    }

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            CMD_CLEAR_EDIT -> barcodeNumber.set("")
            else -> super.command(cmd, data)
        }
    }
}