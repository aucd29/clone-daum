@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import brigitte.di.dagger.module.DaggerViewModelProviders
import brigitte.widget.free
import brigitte.widget.pause
import brigitte.widget.resume
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.*
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject
import kotlin.reflect.KClass

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

abstract class BaseDaggerActivity<T: ViewDataBinding, M: ViewModel> constructor(
) : BaseActivity<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mGlobalDisposable: CompositeDisposable
    @Inject lateinit var mViewModelProviders: DaggerViewModelProviders
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

    override fun supportFragmentInjector() =
        mSupportFragmentInjector

    override fun initViewModel() =
        mViewModelProviders.get(this, mViewModelClass)

    protected inline fun <reified T : ViewModel> inject() =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(this, T::class.java)
        }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerFragment<T: ViewDataBinding, M: ViewModel>
    : BaseFragment<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mViewModelProviders: DaggerViewModelProviders
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun supportFragmentInjector() =
        childFragmentInjector

    override fun initViewModel(): M = mViewModelProviders.let {
        if (mViewModelScope == SCOPE_ACTIVITY) {
            it.get(activity(), mViewModelClass)
        } else {
            it.get(this, mViewModelClass)
        }
    }

    protected inline fun <reified VM : ViewModel> inject() =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(this, VM::class.java)
        }

    protected inline fun <reified VM : ViewModel> activityInject() =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(requireActivity(), VM::class.java)
        }

    protected inline fun <reified VM: ViewModel> stateInject(noinline factory: () -> ViewModelProvider.Factory) =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(this, factory(), VM::class.java)
        }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerDialogFragment<T: ViewDataBinding, M: ViewModel> constructor()
    : BaseDialogFragment<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mViewModelProviders: DaggerViewModelProviders
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun supportFragmentInjector() =
        childFragmentInjector

    override fun initViewModel(): M = mViewModelProviders.let {
        if (mViewModelScope == SCOPE_ACTIVITY) {
            it.get(requireActivity(), mViewModelClass)
        } else {
            it.get(this@BaseDaggerDialogFragment, mViewModelClass)
        }
    }

    protected inline fun <reified VM : ViewModel> inject() =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(this, VM::class.java)
        }

    protected inline fun <reified VM : ViewModel> activityInject() =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(requireActivity(), VM::class.java)
        }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel> constructor()
    : BaseBottomSheetDialogFragment<T, M>(), HasSupportFragmentInjector {

    @Inject lateinit var mViewModelProviders: DaggerViewModelProviders
    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun supportFragmentInjector() =
        childFragmentInjector

    override fun initViewModel(): M = mViewModelProviders.let {
        if (mViewModelScope == SCOPE_ACTIVITY) {
            it.get(requireActivity(), mViewModelClass)
        } else {
            it.get(this@BaseDaggerBottomSheetDialogFragment, mViewModelClass)
        }
    }

    protected inline fun <reified VM : ViewModel> inject() =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(this, VM::class.java)
        }

    protected inline fun <reified VM : ViewModel> activityInject() =
        lazy(LazyThreadSafetyMode.NONE) {
            mViewModelProviders.get(requireActivity(), VM::class.java)
        }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerWebViewFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerWebViewFragment<T: ViewDataBinding, M: ViewModel> constructor(
): BaseDaggerFragment<T, M>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(BaseDaggerWebViewFragment::class.java)
        const val K_URL = "url"
    }

    protected val mUrl: String
        get() = arguments?.getString(K_URL) ?: ""

    abstract val webview: WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            webview.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webview.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()

        webview.apply {
            stopLoading()
            pause()
        }
    }

    override fun onResume() {
        webview.apply {
            resume()
            if (url.isNullOrEmpty() || url.contains(mUrl)) {

                if (mLog.isDebugEnabled) {
                    mLog.debug("RESUME LOAD URL : $mUrl")
                }

                loadUrl(mUrl)
            }
        }

        super.onResume()
    }

    override fun onDestroyView() {
        webview.free()
        super.onDestroyView()
    }
}
