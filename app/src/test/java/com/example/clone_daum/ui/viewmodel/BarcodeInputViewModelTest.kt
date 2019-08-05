package com.example.clone_daum.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.example.clone_daum.MainApp
import com.example.clone_daum.ui.main.mediasearch.barcode.BarcodeInputViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class BarcodeInputViewModelTest {
    lateinit var viewmodel: BarcodeInputViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = BarcodeInputViewModel(app)
    }

    @Test
    fun testEditAction() {
        viewmodel.apply {
            editorAction.get()?.invoke("hellworld")

            val observer = mock(Observer::class.java) as Observer<Pair<String, Any>>
            commandEvent.observeForever(observer)

            command(BarcodeInputViewModel.CMD_HIDE_KEYBOARD)
            verify(observer).onChanged(BarcodeInputViewModel.CMD_HIDE_KEYBOARD to -1)

            verifyNoMoreInteractions(observer)
        }
    }

    @Test
    fun testClearText() {
        viewmodel.apply {
            val observer = mock(Observer::class.java) as Observer<Pair<String, Any>>
            commandEvent.observeForever(observer)

            command(BarcodeInputViewModel.CMD_CLEAR_EDIT)
            verify(observer).onChanged(BarcodeInputViewModel.CMD_CLEAR_EDIT to -1)
            verifyNoMoreInteractions(observer)

            assertEquals(barcodeNumber.get(), "")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(BarcodeInputViewModelTest::class.java)
    }

    private fun initMock() {
        MockitoAnnotations.initMocks(this)
    }

    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()
}