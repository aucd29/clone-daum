package com.example.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.common.di.module.DaggerViewModelFactory
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 19. <p/>
 */

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerRuleActivity
//
////////////////////////////////////////////////////////////////////////////////////

private const val SET_VIEW_MODEL = "setModel"

abstract class BaseDaggerRuleActivity<T: ViewDataBinding, M: ViewModel> : BaseActivity<T>() {
    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    protected lateinit var mViewModel: M

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(viewModelClass())

        bindViewModel()
        snackbarAware()
        dialogAware()
        finishFragmentAware()

        settingEvents()
    }

    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    // 귀차니즘이 너무 큰게 아닌가 싶기도 하고 -_ -ㅋ
    protected open fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(
            argTypes = listOf(viewModelClass()),
            argv     = listOf(mViewModel)
        ))
    }

    override fun onDestroy() {
        mDisposable.clear()

        super.onDestroy()
    }

    protected open fun snackbarAware() = mViewModel.run {
        if (this is ISnackbarAware) {
            observe(snackbarEvent) { snackbar(mBinding.root, it, Snackbar.LENGTH_SHORT)?.show() }
        }
    }

    protected open fun dialogAware() = mViewModel.run {
        if (this is IDialogAware) {
            observeDialog(this.dlgEvent, mDisposable)
        }
    }

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(this.finishEvent) { finish() }
        }
    }

    abstract fun settingEvents()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerFragment<T: ViewDataBinding, M: ViewModel>: BaseRuleFragment<T>() {
    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    lateinit var mViewModel: M

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this.activity!!, mViewModelFactory).get(viewModelClass())

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        snackbarAware()
        dialogAware()
        finishFragmentAware()

        settingEvents()
    }

    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    // 귀차니즘이 너무 큰게 아닌가 싶기도 하고 -_ -ㅋ
    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(
            argTypes = listOf(viewModelClass()),
            argv     = listOf(mViewModel)
        ))
    }

    protected open fun snackbarAware() = mViewModel.run {
        if (this is ISnackbarAware) {
            observe(snackbarEvent) { snackbar(mBinding.root, it, Snackbar.LENGTH_SHORT)?.show() }
        }
    }

    protected open fun dialogAware() = mViewModel.run {
        if (this is IDialogAware) {
            observeDialog(this.dlgEvent, mDisposable)
        }
    }

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(this.finishEvent) { finish() }
        }
    }


    abstract fun settingEvents()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerFragmentDialog
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerDialogFragment<T: ViewDataBinding, M: ViewModel>: BaseRuleDialogFragment<T>() {
    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    lateinit var mViewModel: M

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(activity!!, mViewModelFactory).get(viewModelClass())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        finishFragmentAware()

        settingEvents()
    }

    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(
            argTypes = listOf(viewModelClass()),
            argv     = listOf(mViewModel)
        ))
    }

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(this.finishEvent) { dismiss() }
        }
    }

    abstract fun settingEvents()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseRuleBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel>: BaseRuleBottomSheetDialogFragment<T>() {
    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    lateinit var mViewModel: M

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(activity!!, mViewModelFactory).get(viewModelClass())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        finishFragmentAware()

        settingEvents()
    }

    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(
            argTypes = listOf(viewModelClass()),
            argv     = listOf(mViewModel)
        ))
    }

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(this.finishEvent) { dismiss() }
        }
    }

    abstract fun settingEvents()
}
