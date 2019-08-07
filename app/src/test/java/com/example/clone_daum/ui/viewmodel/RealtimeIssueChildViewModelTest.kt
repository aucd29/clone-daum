package com.example.clone_daum.ui.viewmodel

import androidx.core.text.toHtml
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueChildViewModel
import com.example.clone_daum.util.BaseRoboViewModelTest
import com.example.clone_daum.util.mockReturn
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class RealtimeIssueChildViewModelTest: BaseRoboViewModelTest<RealtimeIssueChildViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = RealtimeIssueChildViewModel(app)
    }

    @Test
    fun typeConvertTest() {
        val issue = mock(RealtimeIssue::class.java)

        issue.apply {
            type.mockReturn("+")
            value.mockReturn("10")
            assertEquals(typeConvert(this), "<font color='red'>↑</font> 10")

            type.mockReturn("-")
            assertEquals(typeConvert(this), "<font color='blue'>↓</font> 10")

            type.mockReturn("N")
            assertEquals(typeConvert(this), "<font color='red'>NEW</font>")
        }
    }

    private fun typeConvert(issue: RealtimeIssue) = viewmodel.typeConvert(issue)?.toHtml()
}