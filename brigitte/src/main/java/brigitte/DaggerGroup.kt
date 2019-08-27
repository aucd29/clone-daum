package brigitte

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import brigitte.di.dagger.module.DaggerViewModelFactory
import brigitte.di.dagger.module.DaggerViewModelProvider
import brigitte.widget.free
import brigitte.widget.pause
import brigitte.widget.resume
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.*
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 19. <p/>
 *
 * Framework Fragment 는 deprecated 되어 삭제 [aucd29][2019-08-21]
 * ViewModelProviders 이 deprecated 되어 삭제 [aucd29][2019-08-22]
 */

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerActivity
//
////////////////////////////////////////////////////////////////////////////////////

/**
 * dagger 를 기본적으로 이용하면서 MVVM 아키텍처를 가지는 Activity
 */

abstract class BaseDaggerActivity<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor(
) : BaseActivity<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mGlobalDisposable: CompositeDisposable
    @Inject lateinit var mViewModelProvider: DaggerViewModelProvider

    /** ViewModel 을 inject 하기 위한 Factory */
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
        mViewModelProvider.get(viewModelClass())

    protected inline fun <reified T : ViewModel> inject()=
        mViewModelProvider.get(T::class.java)

    override fun supportFragmentInjector() =
        mSupportFragmentInjector
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BaseFragment<T, M>(), HasSupportFragmentInjector, BaseViewModelProvider {


    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override val mViewModelProvider: ViewModelProvider by lazy {
        ViewModelProvider(this, mViewModelFactory)
    }

//    @Inject lateinit var mViewModelProviderActivity: dagger.Lazy<DaggerViewModelProvider>
//    override val mViewModelProviderActivity: ViewModelProvider by lazy {
//        ViewModelProvider(requireActivity(), mViewModelFactory)
//    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun supportFragmentInjector() =
        childFragmentInjector

    override fun initViewModel(): M =
        inject(mViewModelScope, viewModelClass())

    protected inline fun <reified VM : ViewModel> inject(activity: FragmentActivity? = null) =
        inject(activity, VM::class.java)

    protected inline fun <reified VM : ViewModel> inject() =
        inject(SCOPE_FRAGMENT, VM::class.java)

    protected inline fun <reified VM : ViewModel> injectOfActivity() =
        inject(SCOPE_ACTIVITY, VM::class.java)
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerDialogFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BaseDialogFragment<T, M>(), HasSupportFragmentInjector, BaseViewModelProvider {

    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override val mViewModelProvider: ViewModelProvider by lazy {
        ViewModelProvider(this, mViewModelFactory)
    }
    override val mViewModelProviderActivity: ViewModelProvider by lazy {
        ViewModelProvider(requireActivity(), mViewModelFactory)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun supportFragmentInjector() =
        childFragmentInjector

    override fun initViewModel(): M =
        inject(mViewModelScope, viewModelClass())

    protected inline fun <reified VM : ViewModel> inject(activity: FragmentActivity? = null) =
        inject(activity, VM::class.java)

    protected inline fun <reified VM : ViewModel> inject(@ViewModelScope scope: Int) =
        inject(scope, VM::class.java)
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BaseBottomSheetDialogFragment<T, M>(), HasSupportFragmentInjector, BaseViewModelProvider {

    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override val mViewModelProvider: ViewModelProvider by lazy {
        ViewModelProvider(this, mViewModelFactory)
    }
    override val mViewModelProviderActivity: ViewModelProvider by lazy {
        ViewModelProvider(requireActivity(), mViewModelFactory)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun supportFragmentInjector() =
        childFragmentInjector

    override fun initViewModel(): M =
        inject(mViewModelScope, viewModelClass())

    protected inline fun <reified VM : ViewModel> inject(activity: FragmentActivity? = null) =
        inject(activity, VM::class.java)

    protected inline fun <reified VM : ViewModel> inject(@ViewModelScope scope: Int) =
        inject(scope, VM::class.java)
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerWebViewFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerWebViewFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor(
): BaseDaggerFragment<T, M>() {
    companion object {
        const val K_URL = "url"
    }

    protected val mUrl: String
        get() = arguments?.getString(K_URL) ?: ""

    abstract val webview: WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            webview.restoreState(savedInstanceState)
        } else {
            webview.loadUrl(mUrl)
        }
    }
//
//    override fun initViewBinding() {
//        webview.run { postDelayed({ loadUrl(mUrl) }, 300)}
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        webview.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        webview.pause()
    }

    override fun onResume() {
        webview.resume()
        super.onResume()
    }

    override fun onDestroyView() {
        webview.free()
        super.onDestroyView()
    }
}
