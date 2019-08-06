package com.example.clone_daum.ui.viewmodel

import androidx.lifecycle.Observer
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.local.FrequentlySiteDao
import com.example.clone_daum.ui.main.navigation.shortcut.FrequentlySiteViewModel
import com.example.clone_daum.util.*
import io.reactivex.disposables.CompositeDisposable
import junit.framework.TestCase.assertEquals
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
class FrequentlySiteViewModelTest: BaseRoboViewModelTest<FrequentlySiteViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = FrequentlySiteViewModel(app, dao)
    }

    @Test
    fun gridCountTest() {
        viewmodel.gridCount.get() eq 5
    }

    @Test
    fun initTest() {

    }

    @Test
    fun eventIconTextTest() {
        mock(FrequentlySite::class.java).apply {
            title.mockReturn(FrequentlySiteViewModel.DEFAULT_TITLE)
            viewmodel.eventIconText(this) eq "http"

            title.mockReturn("ANOTHER")
            url.mockReturn("http://test.net")
            viewmodel.eventIconText(this) eq "T"
        }
    }

    @Test
    fun eventOpenTest() {
        val url = "http://test.net"

        viewmodel.apply {
            eventOpen(url)

            mockObserver<String>(brsOpenEvent).apply {
                verifyChanged(url)
                verifyNoMoreInteractions(this)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Mock lateinit var dao: FrequentlySiteDao
}