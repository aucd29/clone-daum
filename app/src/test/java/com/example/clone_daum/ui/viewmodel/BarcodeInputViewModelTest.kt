package com.example.clone_daum.ui.viewmodel

import brigitte.DialogParam
import com.example.clone_daum.R
import com.example.clone_daum.ui.main.mediasearch.barcode.BarcodeInputViewModel
import brigitte.shield.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class BarcodeInputViewModelTest: BaseRoboViewModelTest<BarcodeInputViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = BarcodeInputViewModel(app)
    }

    @Test
    fun editActionTest() {
        viewmodel.apply {
            mockObserver<DialogParam>(dialogEvent).apply {
                editorAction.get()?.invoke("helloworld")

                val param = DialogParam(context = app,
                    messageId = R.string.barcode_not_matched_info,
                    titleId = R.string.barcode_not_detacted)

                if (mLog.isDebugEnabled) {
                    mLog.debug("EVENT ${dialogEvent.value?.messageId}")
                    mLog.debug("PARAM ${param.messageId}")
                }

                verifyChanged(param)
            }
        }
    }

    @Test
    fun clearTextTest() {
        viewmodel.apply {
            barcodeNumber.set("helloworld")

            if (mLog.isDebugEnabled) {
                mLog.debug("BEFORE ${barcodeNumber.get()}")
            }

            command(BarcodeInputViewModel.CMD_CLEAR_EDIT)

            if (mLog.isDebugEnabled) {
                mLog.debug("AFTER ${barcodeNumber.get()}")
            }

            barcodeNumber.get().assertEquals("")
        }
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(BarcodeInputViewModelTest::class.java)
    }
}