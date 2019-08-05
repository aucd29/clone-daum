package com.example.clone_daum.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.text.toHtml
import androidx.test.core.app.ApplicationProvider
import com.example.clone_daum.MainApp
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueChildViewModel
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
class RealtimeIssueChildViewModelTest {
    lateinit var viewmodel: RealtimeIssueChildViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = RealtimeIssueChildViewModel(app)
    }

    @Test
    fun testTypeConvert() {
        val issue = mock(RealtimeIssue::class.java)

        `when`(issue.type).thenReturn("+")
        `when`(issue.value).thenReturn("10")
        assertEquals(viewmodel.typeConvert(issue)?.toHtml(), "<font color='red'>↑</font> 10")

        `when`(issue.type).thenReturn("-")
        `when`(issue.value).thenReturn("10")
        assertEquals(viewmodel.typeConvert(issue)?.toHtml(), "<font color='blue'>↓</font> 10")

        `when`(issue.type).thenReturn("N")
        `when`(issue.value).thenReturn("10")
        assertEquals(viewmodel.typeConvert(issue)?.toHtml(), "<font color='red'>NEW</font>")
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueChildViewModelTest::class.java)
    }

    private fun initMock() {
        MockitoAnnotations.initMocks(this)

    }


    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()
}