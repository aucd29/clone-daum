package com.example.clone_daum.ui.viewmodel

import androidx.core.text.toHtml
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import com.example.clone_daum.util.BaseRoboViewModelTest
import com.example.clone_daum.util.mockReturn
import com.google.android.material.tabs.TabLayout
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class RealtimeIssueViewModelTest: BaseRoboViewModelTest<RealtimeIssueViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = RealtimeIssueViewModel(app, config)
    }

    @Test
    fun tabChangedCallbackTest() {
        val tab = mock(TabLayout.Tab::class.java)

        repeat(2) {
            tab.position.mockReturn(it)

            viewmodel.apply {
                tabChangedCallback.get()?.onTabSelected(tab)
                assertEquals(tabChangedLive.value?.position, it)
            }
        }
    }

    @Test
    fun loadTest() {

    }

    @Test
    fun titleConvertTest() {
        assertEquals(viewmodel.titleConvert(null), "")

        val issue = mock(RealtimeIssue::class.java)
        issue.index.mockReturn(1)
        issue.text.mockReturn("hello")

        assertEquals(viewmodel.titleConvert(issue), "1 hello")
    }

    @Test
    fun typeConvertTest() {
        val issue = mock(RealtimeIssue::class.java)

        issue.apply {
            type.mockReturn("+")
            value.mockReturn("10")
            assertEquals(viewmodel.typeConvert(issue)?.toHtml(), "<font color='red'>↑</font> 10")

            type.mockReturn("-")
            assertEquals(viewmodel.typeConvert(issue)?.toHtml(), "<font color='blue'>↓</font> 10")

            type.mockReturn("N")
            assertEquals(viewmodel.typeConvert(issue)?.toHtml(), "<font color='red'>NEW</font>")
        }
    }

    @Test
    fun layoutTranslationYTest() {
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

    @Mock lateinit var config: PreloadConfig
}