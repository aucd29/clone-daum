package com.example.clone_daum.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.MainApp
import com.example.clone_daum.ui.search.PopularViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class PopularViewModelTest {
    lateinit var viewmodel: PopularViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = PopularViewModel(app, layoutmanager)
    }

    @Test
    fun testLoad() {

    }

    @Test
    fun testInit() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(PopularViewModelTest::class.java)
    }

    @Mock lateinit var layoutmanager: ChipsLayoutManager

    private fun initMock() {
        MockitoAnnotations.initMocks(this)
    }

    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()
}