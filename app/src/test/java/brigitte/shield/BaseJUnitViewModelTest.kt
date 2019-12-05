package brigitte.shield

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import org.junit.Rule
import org.mockito.MockitoAnnotations

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-23 <p/>
 */

open class BaseJUnitViewModelTest<T: ViewModel> constructor() {
    lateinit var viewmodel: T

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    protected fun initMock() {
        MockitoAnnotations.initMocks(this)
    }
}