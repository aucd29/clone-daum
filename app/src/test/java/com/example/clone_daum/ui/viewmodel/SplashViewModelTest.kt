package com.example.clone_daum.ui.viewmodel

import androidx.lifecycle.Observer
import com.example.clone_daum.ui.main.SplashViewModel
import brigitte.shield.BaseJUnitViewModelTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.*


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-23 <p/>
 *
 * https://github.com/fabioCollini/DaggerMock
 * https://stackoverflow.com/questions/50950654/androidviewmodel-and-unit-tests
 * https://fernandocejas.com/2014/04/08/unit-testing-asynchronous-methods-with-mockito/
 */

@RunWith(JUnit4::class)
class SplashViewModelTest: BaseJUnitViewModelTest<SplashViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = SplashViewModel()
    }

    @Test
    fun closeTest() {
        val observer = mock(Observer::class.java) as Observer<Void>
        viewmodel.closeEvent.observeForever(observer)
        viewmodel.closeSplash()

        verify(observer, atLeastOnce()).onChanged(null)
    }
}