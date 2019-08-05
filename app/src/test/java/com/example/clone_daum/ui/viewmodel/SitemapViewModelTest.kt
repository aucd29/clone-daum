package com.example.clone_daum.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.example.clone_daum.MainApp
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.Sitemap
import com.example.clone_daum.ui.main.navigation.shortcut.SitemapViewModel
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
class SitemapViewModelTest {
    lateinit var viewmodel: SitemapViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = SitemapViewModel(app, config)
    }

    @Test
    fun testEventOpenUrl() {
        val sitemap = mock(Sitemap::class.java)
        `when`(sitemap.isApp).thenReturn(false)
        `when`(sitemap.url).thenReturn("http://url")

        viewmodel.apply {
            eventOpen(sitemap)

            val observer = mock(Observer::class.java) as Observer<String>
            brsOpenEvent.observeForever(observer)
            verify(observer).onChanged("http://url")

            verifyNoMoreInteractions(observer)
        }
    }

    @Test
    fun testEventOpenApp() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(SitemapViewModelTest::class.java)
    }

    @Mock lateinit var config: PreloadConfig

    private fun initMock() {
        MockitoAnnotations.initMocks(this)
        `when`(config.naviSitemapList).thenReturn(arrayListOf())
    }

    private val app = ApplicationProvider.getApplicationContext<MainApp>()
}