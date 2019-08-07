package com.example.clone_daum.ui.viewmodel

import androidx.lifecycle.Observer
import com.example.clone_daum.ui.main.mediasearch.barcode.BarcodeInputViewModel
import com.example.clone_daum.util.BaseRoboViewModelTest
import com.example.clone_daum.util.mockObserver
import com.example.clone_daum.util.verifyChanged
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

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
            editorAction.get()?.invoke("helloworld")

            mockObserver<Pair<String, Any>>(commandEvent).apply {
                verifyChanged(viewmodel, BarcodeInputViewModel.CMD_HIDE_KEYBOARD)
                verifyNoMoreInteractions(this)
            }
        }
    }

    @Test
    fun clearTextTest() {
        viewmodel.apply {
            command(BarcodeInputViewModel.CMD_CLEAR_EDIT)
            assertEquals(barcodeNumber.get(), "")
        }
    }
}