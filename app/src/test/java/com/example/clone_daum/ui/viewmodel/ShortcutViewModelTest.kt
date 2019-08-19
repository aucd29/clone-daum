package com.example.clone_daum.ui.viewmodel

import androidx.lifecycle.Observer
import brigitte.DialogParam
import com.example.clone_daum.R
import com.example.clone_daum.ui.main.navigation.shortcut.ShortcutViewModel
import brigitte.shield.BaseRoboViewModelTest
import brigitte.shield.mockObserver
import brigitte.shield.verifyChanged
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class ShortcutViewModelTest: BaseRoboViewModelTest<ShortcutViewModel>()  {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = ShortcutViewModel(app)
    }

    @Test
    fun eventSitemapTest() {
        viewmodel.apply {
            eventSitemap()

            mockObserver<String>(brsSitemapEvent).apply {
                verifyChanged("https://m.daum.net/site.daum")
            }
        }
    }

    @Test
    fun eventFrequentlySiteTest() {
        viewmodel.apply {
            eventFrequentlySite()

//            val dlgparam = mock(DialogParam::class.java)
//            `when`(dlgparam.context).thenReturn(app)
//            `when`(dlgparam.messageId).thenReturn(R.string.shortcut_link_history)

            val dlgparam = DialogParam(context = app, messageId = R.string.shortcut_link_history)

            mockObserver<DialogParam>(dialogEvent).apply {
                verifyChanged(dlgparam)
            }
        }
    }
}