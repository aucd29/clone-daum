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
private const val LAYOUT         = "layout"

abstract class BaseDaggerRuleActivity<T: ViewDataBinding, M: ViewModel>
    : BaseActivity<T>() {

    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    protected lateinit var mViewModel: M
    protected var mLayoutName = generateLayoutName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(viewModelClass())

        bindViewModel()

        snackbarAware()
        dialogAware()
        finishFragmentAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun layoutId() = resources.getIdentifier(mLayoutName, LAYOUT, packageName)

    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    protected open fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(viewModelClass(), mViewModel))
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
            observeDialog(dlgEvent, mDisposable)
        }
    }

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(finishEvent) { finish() }
        }
    }

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerFragment<T: ViewDataBinding, M: ViewModel>
    : BaseFragment<T>() {

    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    protected var mLayoutName = generateLayoutName()

    protected lateinit var mViewModel: M

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = viewModelProvider()

        if (layoutId() == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        snackbarAware()
        dialogAware()
        finishFragmentAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun layoutId() =
        resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)

    protected open fun viewModelProvider() =
        ViewModelProviders.of(this.activity!!, mViewModelFactory).get(viewModelClass())

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    // 귀차니즘이 너무 큰게 아닌가 싶기도 하고 -_ -ㅋ
    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(viewModelClass(), mViewModel))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    protected open fun snackbarAware() = mViewModel.run {
        if (this is ISnackbarAware) {
            observe(snackbarEvent) { snackbar(mBinding.root, it, Snackbar.LENGTH_SHORT)?.show() }
        }
    }

    protected open fun dialogAware() = mViewModel.run {
        if (this is IDialogAware) {
            observeDialog(dlgEvent, mDisposable)
        }
    }

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(finishEvent) { finish() }
        }
    }

    protected fun commandEventAware() = mViewModel.run {
        if (this is ICommandEventAware) {
            observe(commandEvent) { onCommandEvent(it) }
        }
    }

    protected open fun onCommandEvent(cmd: String) {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerFragmentDialog
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerDialogFragment<T: ViewDataBinding, M: ViewModel>
    : BaseDialogFragment<T>() {

    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    protected lateinit var mViewModel: M
    protected var mLayoutName = generateLayoutName()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = viewModelProvider()

        if (layoutId() == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        finishFragmentAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun layoutId() = resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)

    protected open fun viewModelProvider() =
        ViewModelProviders.of(this.activity!!, mViewModelFactory).get(viewModelClass())

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(viewModelClass(), mViewModel))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(finishEvent) { dismiss() }
        }
    }

    protected fun commandEventAware() = mViewModel.run {
        if (this is ICommandEventAware) {
            observe(commandEvent) { onCommandEvent(it) }
        }
    }

    protected open fun onCommandEvent(cmd: String) {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseRuleBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel>
    : BaseBottomSheetDialogFragment<T>() {

    @Inject lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    protected lateinit var mViewModel: M

    protected var mLayoutName = generateLayoutName()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = viewModelProvider()

        if (layoutId() == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        finishFragmentAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    protected open fun viewModelProvider() =
        ViewModelProviders.of(this.activity!!, mViewModelFactory).get(viewModelClass())

    override fun layoutId() = resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)

    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(viewModelClass(), mViewModel))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    protected open fun finishFragmentAware() = mViewModel.run {
        if (this is IFinishFragmentAware) {
            observe(finishEvent) { dismiss() }
        }
    }

    protected fun commandEventAware() = mViewModel.run {
        if (this is ICommandEventAware) {
            observe(commandEvent) { onCommandEvent(it) }
        }
    }

    protected open fun onCommandEvent(cmd: String) {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
}
