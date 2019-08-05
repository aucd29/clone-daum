package com.example.clone_daum.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.example.clone_daum.MainApp
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.FrequentlySiteDao
import com.example.clone_daum.ui.main.navigation.shortcut.FrequentlySiteViewModel
import io.reactivex.disposables.CompositeDisposable
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
class FrequentlySiteViewModelTest {
    lateinit var viewmodel: FrequentlySiteViewModel



    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        val disposable = CompositeDisposable()
        viewmodel = FrequentlySiteViewModel(app, dao)
    }

    @Test
    fun testGridCount() {
        assertEquals(viewmodel.gridCount.get(), 5)
    }

    @Test
    fun testInit() {

    }

    @Test
    fun testEventIconText() {
        val item = mock(FrequentlySite::class.java)
        `when`(item.title).thenReturn(FrequentlySiteViewModel.DEFAULT_TITLE)
        assertEquals(viewmodel.eventIconText(item), "http")

        `when`(item.title).thenReturn("http://test.net")
        assertEquals(viewmodel.eventIconText(item), "TEST.NET")
    }

    @Test
    fun testEventOpen() {
        val url = "http://test.net"

        viewmodel.apply {
            eventOpen(url)

            val observer = mock(Observer::class.java) as Observer<String>
            brsOpenEvent.observeForever(observer)
            verify(observer).onChanged( url)
            verifyNoMoreInteractions(observer)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(FrequentlySiteViewModelTest::class.java)
    }

    @Mock lateinit var dao: FrequentlySiteDao

    private fun initMock() {
        MockitoAnnotations.initMocks(this)

    }


    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()

}