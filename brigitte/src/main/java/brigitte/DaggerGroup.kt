package brigitte

import android.content.Context
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import brigitte.di.dagger.module.DaggerViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.*
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 19. <p/>
 */

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerActivity
//
////////////////////////////////////////////////////////////////////////////////////

private const val SET_VIEW_MODEL  = "setModel"       // view model 을 설정하기 위한 메소드 명
private const val LAYOUT          = "layout"         // 레이아웃

/**
 * dagger 를 기본적으로 이용하면서 MVVM 아키텍처를 가지는 Activity
 */

// Framework Fragment 는 deprecated 되어 삭제 [aucd29][2019-08-21]
abstract class BaseDaggerActivity<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BaseActivity<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mGlobalDisposable: CompositeDisposable

    /** ViewModel 을 inject 하기 위한 Factory */
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    @Inject lateinit var mSupportFragmentInjector: DispatchingAndroidInjector<Fragment>

    /**
     * view model 처리, 기본 이벤트 처리를 위한 aware 와 view binding, view model 을 호출 한다.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        mGlobalDisposable.clear()

        super.onDestroy()
    }

    override fun initViewModel() =
        ViewModelProviders.of(this, mViewModelFactory).get(viewModelClass())

    protected inline fun <reified T : ViewModel> inject()=
        ViewModelProviders.of(this, mViewModelFactory).get(T::class.java)

    override fun supportFragmentInjector() =
        mSupportFragmentInjector
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BaseFragment<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun initViewModel() = when (mViewModelScope) {
        SCOPE_FRAGMENT -> ViewModelProviders.of(this, mViewModelFactory)
        else -> ViewModelProviders.of(requireActivity(), mViewModelFactory)
    }.get(viewModelClass())

    protected inline fun <reified T : ViewModel> inject(activity: FragmentActivity? = null) = activity?.run {
        ViewModelProviders.of(this@run, mViewModelFactory).get(T::class.java)
    } ?: ViewModelProviders.of(this, mViewModelFactory).get(T::class.java)

    override fun supportFragmentInjector() = childFragmentInjector
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerDialogFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BaseDialogFragment<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun initViewModel() = when (mViewModelScope) {
        BaseFragment.SCOPE_FRAGMENT -> ViewModelProviders.of(this, mViewModelFactory)
        else -> ViewModelProviders.of(requireActivity(), mViewModelFactory)
    }.get(viewModelClass())

    protected inline fun <reified T : ViewModel> inject(activity: FragmentActivity? = null) = activity?.run {
        ViewModelProviders.of(this@run, mViewModelFactory).get(T::class.java)
    } ?: ViewModelProviders.of(this, mViewModelFactory).get(T::class.java)

    override fun supportFragmentInjector() =
        childFragmentInjector
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BaseBottomSheetDialogFragment<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun onDestroyView() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()

        super.onDestroyView()
    }

    override fun initViewModel() = when (mViewModelScope) {
        BaseFragment.SCOPE_FRAGMENT -> ViewModelProviders.of(this, mViewModelFactory)
        else -> ViewModelProviders.of(requireActivity(), mViewModelFactory)
    }.get(viewModelClass())

    protected inline fun <reified T : ViewModel> inject(activity: FragmentActivity? = null) = activity?.run {
        ViewModelProviders.of(this@run, mViewModelFactory).get(T::class.java)
    } ?: ViewModelProviders.of(this, mViewModelFactory).get(T::class.java)

    override fun supportFragmentInjector() =
        childFragmentInjector
}
