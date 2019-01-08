@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.example.common.arch.SingleLiveEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 10. 15. <p/>
 */

private const val LAYOUT = "layout"

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(@StringRes resid: Int) =
    app.getString(resid)

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(resid: String) =
    app.string(resid)

inline val AndroidViewModel.app : Application
        get() = getApplication()

inline fun AndroidViewModel.time() = System.currentTimeMillis()

inline fun AndroidViewModel.dpToPx(v: Int) = v * app.displayDensity()
inline fun AndroidViewModel.pxToDp(v: Int) = v / app.displayDensity()

////////////////////////////////////////////////////////////////////////////////////
//
// VIEW MODEL
//
////////////////////////////////////////////////////////////////////////////////////

inline fun <reified T : ViewModel> FragmentActivity.viewModel(clazz: Class<T>,
                                                      provider: ViewModelProvider.Factory? = null) =
        provider?.let { ViewModelProviders.of(this, it).get(clazz) } ?:
                        ViewModelProviders.of(this).get(clazz)

inline fun <reified T : ViewModel> Fragment.viewModel(clazz: Class<T>,
                                              provider: ViewModelProvider.Factory? = null) =
        activity?.viewModel(clazz, provider)


////////////////////////////////////////////////////////////////////////////////////
//
// DATA BINDING
//
////////////////////////////////////////////////////////////////////////////////////


inline fun <T : ViewDataBinding> Fragment.dataBinding(@LayoutRes layoutid: Int, parent: ViewGroup? = null,
                                                      attachToParent: Boolean = false): T =
        DataBindingUtil.inflate(layoutInflater, layoutid, parent, attachToParent)

inline fun <T : ViewDataBinding> Activity.dataBinding(@LayoutRes layoutid: Int, parent: ViewGroup? = null,
                                                      attachToParent: Boolean = false): T =
        DataBindingUtil.inflate(layoutInflater, layoutid, parent, attachToParent)

inline fun <T : ViewDataBinding> Activity.dataBindingView(@LayoutRes layoutid: Int): T =
        DataBindingUtil.setContentView(this, layoutid)



////////////////////////////////////////////////////////////////////////////////////
//
// AWARE INTERFACES
//
////////////////////////////////////////////////////////////////////////////////////


interface IDialogAware {
    val dlgEvent: SingleLiveEvent<DialogParam>
}

interface ISnackbarAware {
    val snackbarEvent: SingleLiveEvent<String>

    fun snackbarEvent(msg: String?) {
        msg?.let { snackbarEvent.value = it }
    }
}

interface IFinishFragmentAware {
    val finishEvent: SingleLiveEvent<Void>
}

interface OnBackPressedListener {
    fun onBackPressed(): Boolean
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseActivity
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseActivity<T : ViewDataBinding>
    : DaggerAppCompatActivity() {

    protected lateinit var mBinding : T
    protected lateinit var mBackPressed: BackPressedManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding     = dataBindingView(layoutId())
        mBackPressed = BackPressedManager(this, mBinding.root)
    }

    fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    override fun onBackPressed() {
        // 현재 fragment 가 OnBackPressedListener 를 상속 받고 return true 를 하면 인터페이스에서
        // h/w backkey 를 처리한 것으로 본다.
        val frgmt = supportFragmentManager.current
        if (frgmt != null && frgmt is OnBackPressedListener && frgmt.onBackPressed()) {
            return
        }

        if (mBackPressed.onBackPressed()) {
            super.onBackPressed()
        }
    }


    abstract fun layoutId(): Int
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseFragment<T: ViewDataBinding>
    : DaggerFragment() {

    protected lateinit var mBinding : T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    protected fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    protected fun activity() = activity as BaseActivity<out ViewDataBinding>
    fun inflate(@LayoutRes resid: Int, root: ViewGroup? = null) = layoutInflater.inflate(resid, root)

    protected abstract fun layoutId(): Int
    protected abstract fun bindViewModel()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDialogFragment<T: ViewDataBinding> : DaggerAppCompatDialogFragment() {
    protected lateinit var mBinding : T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    fun activity() = activity as BaseActivity<out ViewDataBinding>
    fun inflate(@LayoutRes resid: Int, root: ViewGroup? = null) = layoutInflater.inflate(resid, root)

    abstract fun layoutId(): Int
    abstract fun bindViewModel()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseBottomSheetDialogFragment<T: ViewDataBinding>
    : BottomSheetDialogFragment(), HasSupportFragmentInjector {

    protected lateinit var mBinding : T

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun supportFragmentInjector() = childFragmentInjector

    fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    fun activity() = activity as BaseActivity<out ViewDataBinding>
    fun inflate(@LayoutRes resid: Int, root: ViewGroup? = null) = layoutInflater.inflate(resid, root)

    abstract fun layoutId(): Int
    abstract fun bindViewModel()
}
