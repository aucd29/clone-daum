package com.example.clone_daum.ui.viewmodel

import androidx.core.text.toHtml
import androidx.test.core.app.ApplicationProvider
import com.example.clone_daum.MainApp
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import com.google.android.material.tabs.TabLayout
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class RealtimeIssueViewModelTest {
    lateinit var viewmodel: RealtimeIssueViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = RealtimeIssueViewModel(app, config)
    }

    @Test
    fun testTabChanged() {
        val tab = mock(TabLayout.Tab::class.java)

        repeat(2) {
            `when`(tab.position).thenReturn(it)

            viewmodel.apply {
                tabChangedCallback.get()?.onTabSelected(tab)
                assertEquals(tabChangedLive.value?.position, it)
            }
        }
    }

    @Test
    fun testLoad() {

    }

    @Test
    fun testTitleConvert() {
        assertEquals(viewmodel.titleConvert(null), "")

        val issue = mock(RealtimeIssue::class.java)
        `when`(issue.index).thenReturn(1)
        `when`(issue.text).thenReturn("hello")

        assertEquals(viewmodel.titleConvert(issue), "1 hello")
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

    @Test
    fun testLayoutTranslationY() {
        viewmodel.apply {
            layoutTranslationY(10f)
            assertEquals(layoutTranslationY.get(), 10f)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueViewModelTest::class.java)
    }

    @Mock lateinit var config: PreloadConfig

    private fun initMock() {
        MockitoAnnotations.initMocks(this)
    }

    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()
}