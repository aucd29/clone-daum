package com.example.clone_daum.ui.viewmodel

import androidx.core.text.toHtml
import brigitte.html
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueChildViewModel
import brigitte.shield.BaseRoboViewModelTest
import brigitte.shield.assertEquals
import brigitte.shield.assertTrue
import brigitte.shield.mockReturn
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-05 <p/>
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
            viewmodel.typeConvert(this)?.toHtml()
                .assertEquals("<font color='red'>↑</font> $value".html()?.toHtml())

            type.mockReturn("-")
            viewmodel.typeConvert(this)?.toHtml()
                .assertEquals("<font color='blue'>↓</font> $value".html()?.toHtml())

            type.mockReturn("N")
            viewmodel.typeConvert(this)?.toHtml()
                .assertEquals("<font color='red'>NEW</font>".html()?.toHtml())
        }
    }
}