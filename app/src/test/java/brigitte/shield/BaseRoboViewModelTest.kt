package brigitte.shield

import androidx.lifecycle.ViewModel

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-23 <p/>
 */

open class BaseRoboViewModelTest<T: ViewModel>: BaseRoboTest() {
    lateinit var viewmodel: T
}
