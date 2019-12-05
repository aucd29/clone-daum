@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.ViewModelStoreOwnerDefinition
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-05-22 <p/>
 *
 *
 * https://github.com/InsertKoinIO/koin/issues/56
 */

// koin 현재 버전에는 없는데 git 내에는 이 코드가 존재 함
// 나중에 제거해야 되는 듯
inline fun <T : ViewModel> Fragment.getSharedViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    noinline from: ViewModelStoreOwnerDefinition = { activity as ViewModelStoreOwner },
    noinline parameters: ParametersDefinition? = null
): T {
    val koin = getKoin()
    return koin.getViewModel(
            ViewModelParameters(
                    clazz,
                    this@getSharedViewModel,
                    qualifier,
                    from,
                    parameters
            )
    )
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

private const val SET_VIEW_MODEL = "setModel"       // view model 을 설정하기 위한 메소드 명
private const val LAYOUT         = "layout"         // 레이아웃

abstract class BaseKoinActivity<T: ViewDataBinding, M: ViewModel>
    : BaseActivity<T, M>() {
    override fun initViewModel() = getViewModel(mViewModelClass.kotlin)
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseKoinFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseKoinFragment<T: ViewDataBinding, M: ViewModel>
    : BaseFragment<T, M>() {
    override fun initViewModel() =
        if (mViewModelScope == SCOPE_FRAGMENT)
            getViewModel(mViewModelClass.kotlin)
        else
            getSharedViewModel(mViewModelClass.kotlin)
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseKoinDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseKoinDialogFragment<T: ViewDataBinding, M: ViewModel>
    : BaseDialogFragment<T, M>() {

    override fun initViewModel() =
        if (mViewModelScope == SCOPE_FRAGMENT)
            getViewModel(mViewModelClass.kotlin)
        else
            getSharedViewModel(mViewModelClass.kotlin)
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseKoinBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseKoinBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel>
    : BaseBottomSheetDialogFragment<T, M>() {

    override fun initViewModel() =
        if (mViewModelScope == SCOPE_FRAGMENT)
            getViewModel(mViewModelClass.kotlin)
        else
            getSharedViewModel(mViewModelClass.kotlin)
}

