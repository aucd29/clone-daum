package brigitte.viewmodel

import androidx.lifecycle.Observer
import brigitte.shield.BaseJUnitViewModelTest
import brigitte.shield.BaseRoboViewModelTest
import brigitte.shield.verifyChanged
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner


/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-23 <p/>
 *
 * https://github.com/fabioCollini/DaggerMock
 * https://stackoverflow.com/questions/50950654/androidviewmodel-and-unit-tests
 * https://fernandocejas.com/2014/04/08/unit-testing-asynchronous-methods-with-mockito/
 */

@RunWith(RobolectricTestRunner::class)
class SplashViewModelTest: BaseRoboViewModelTest<SplashViewModel>() {
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