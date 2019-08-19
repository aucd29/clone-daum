package com.example.clone_daum.ui.viewmodel

import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.FrequentlySiteDao
import com.example.clone_daum.ui.main.navigation.shortcut.FrequentlySiteViewModel
import brigitte.shield.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class FrequentlySiteViewModelTest: BaseRoboViewModelTest<FrequentlySiteViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = FrequentlySiteViewModel(app, dao)
    }

    @Test
    fun defaultGridCountTest() {
        viewmodel.gridCount.get().assertEquals(5)
    }

    @Test
    fun initTest() {

    }

    @Test
    fun eventIconTextTest() {
        mock(FrequentlySite::class.java).apply {
            title.mockReturn(FrequentlySiteViewModel.DEFAULT_TITLE)
            viewmodel.eventIconText(this).assertEquals("http")

            title.mockReturn("ANOTHER")
            url.mockReturn("http://test.net")
            viewmodel.eventIconText(this).assertEquals("T")
        }
    }

    @Test
    fun eventOpenTest() {
        val url = "http://test.net"

        viewmodel.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("EVENT URL : $url")
            }

            eventOpen(url)

            mockObserver<String>(brsOpenEvent).apply {
                verifyChanged(url)

                if (mLog.isDebugEnabled) {
                    mLog.debug("OBSERVE URL : ${brsOpenEvent.value}")
                }
            }
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
}