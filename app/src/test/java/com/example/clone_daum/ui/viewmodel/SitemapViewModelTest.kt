package com.example.clone_daum.ui.viewmodel

import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.Sitemap
import com.example.clone_daum.ui.main.navigation.shortcut.SitemapViewModel
import brigitte.shield.BaseRoboViewModelTest
import brigitte.shield.mockObserver
import brigitte.shield.verifyChanged
import brigitte.shield.mockReturn
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class SitemapViewModelTest: BaseRoboViewModelTest<SitemapViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = SitemapViewModel(app, config)
    }

    @Test
    fun eventOpenUrlTest() {
        val sitemap = mock(Sitemap::class.java)

        sitemap.apply {
            isApp.mockReturn(false)
            url.mockReturn("http://url")
        }

        viewmodel.apply {
            eventOpen(sitemap)

            mockObserver<String>(brsOpenEvent).apply {
                verifyChanged("http://url")
            }
        }
    }

    @Test
    fun eventOpenAppTest() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Mock lateinit var config: PreloadConfig

    protected override fun initMock() {
        super.initMock()

        config.naviSitemapList.mockReturn(arrayListOf())
    }
}