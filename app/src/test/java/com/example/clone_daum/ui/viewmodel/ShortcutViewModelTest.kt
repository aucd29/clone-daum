package com.example.clone_daum.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import brigitte.DialogParam
import com.example.clone_daum.MainApp
import com.example.clone_daum.R
import com.example.clone_daum.ui.main.navigation.shortcut.ShortcutViewModel
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
class ShortcutViewModelTest {
    lateinit var viewmodel: ShortcutViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = ShortcutViewModel(app)
    }

    @Test
    fun testEventSitemap() {
        viewmodel.apply {
            eventSitemap()

            val observer = mock(Observer::class.java) as Observer<String>
            brsSitemapEvent.observeForever(observer)
            verify(observer).onChanged( "https://m.daum.net/site.daum")
            verifyNoMoreInteractions(observer)
        }
    }

    @Test
    fun testEventFrequentlySite() {
        viewmodel.apply {
            eventFrequentlySite()

//            val dlgparam = mock(DialogParam::class.java)
//            `when`(dlgparam.context).thenReturn(app)
//            `when`(dlgparam.messageId).thenReturn(R.string.shortcut_link_history)

            val dlgparam = DialogParam(context = app, messageId = R.string.shortcut_link_history)
            val observer = mock(Observer::class.java) as Observer<DialogParam>
            dialogEvent.observeForever(observer)
            verify(observer).onChanged(dlgparam)
            verifyNoMoreInteractions(observer)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(ShortcutViewModelTest::class.java)
    }

    private fun initMock() {
        MockitoAnnotations.initMocks(this)
    }

    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()

}