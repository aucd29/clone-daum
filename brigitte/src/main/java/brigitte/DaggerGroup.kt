package brigitte

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import brigitte.di.module.DaggerViewModelFactory
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 19. <p/>
 */

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDaggerRuleActivity
//
////////////////////////////////////////////////////////////////////////////////////

private const val SET_VIEW_MODEL  = "setModel"       // view model 을 설정하기 위한 메소드 명
private const val LAYOUT          = "layout"         // 레이아웃

/**
 * dagger 를 기본적으로 이용하면서 MVVM 아키텍처를 가지는 Activity
 */
abstract class BaseDaggerRuleActivity<T: ViewDataBinding, M: ViewModel>
    : BaseActivity<T>() {

    /** 전역 CompositeDisposable */
    @Inject lateinit var mDisposable: CompositeDisposable

    /** ViewModel 을 inject 하기 위한 Factory */
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    /** onCreate 에서 inject 한 view model */
    protected lateinit var mViewModel: M

    /** setContentView 를 하기 위한 layout 이름 */
    protected var mLayoutName = generateLayoutName()

    /**
     * view model 처리, 기본 이벤트 처리를 위한 aware 와 view binding, view model 을 호출 한다.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(viewModelClass())

        bindViewModel()

        dialogAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    /**
     * activity class 이름에 해당하는 layout 리소스 아이디를 반환 한다.
     */
    override fun layoutId() = resources.getIdentifier(mLayoutName, LAYOUT, packageName)

    /**
     * ViewModel 클래스 명을 얻는다.
     */
    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    /**
     * viewModel 을 ViewDataBinding 에 설정 한다.
     */
    protected open fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(viewModelClass(), mViewModel))
    }

    /**
     * 앱 종료 시 CompositeDisposable 를 clear 한다.
     */
    override fun onDestroy() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()

        super.onDestroy()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * view model 에 등록되어 있는 dialog live event 의 값에 변화를 감지하여 이벤트를 발생 시킨다.
     */
    protected open fun dialogAware() = mViewModel.run {
        if (this is IDialogAware) {
            observeDialog(dialogEvent, mDisposable)
        }
    }

    /**
     * view model 에 등록되어 있는 command live event 의 값에 변화를 감지하여 이벤트를 발생 시킨다.
     */
    protected fun commandEventAware() = mViewModel.run {
        if (this is ICommandEventAware) {
            observe(commandEvent) {
                when (it.first) {
                    ICommandEventAware.CMD_FINISH   -> commandFinish()
                    ICommandEventAware.CMD_TOAST    -> commandToast(it.second.toString())
                    ICommandEventAware.CMD_SNACKBAR -> commandSnackbar(it.second.toString())

                    else -> onCommandEvent(it.first, it.second)
                }
            }
        }
    }

    protected open fun commandFinish() = finish()
    protected open fun commandToast(message: String) = toast(message)
    protected open fun commandSnackbar(message: String) =
        snackbar(mBinding.root, message).show()

    protected open fun onCommandEvent(cmd: String, data: Any) { }

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
// BaseDaggerFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDaggerFragment<T: ViewDataBinding, M: ViewModel>
    : BaseFragment<T>() {

    companion object {
        const val SCOPE_ACTIVITY = 0
        const val SCOPE_FRAGMENT = 1
    }

    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory

    protected lateinit var mViewModel: M
    protected lateinit var mDisposable: CompositeDisposable

    protected var mLayoutName     = generateLayoutName()
    protected var mViewModelScope = SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDisposable = CompositeDisposable()
        mViewModel  = viewModelProvider()

        if (layoutId() == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialogAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun onDestroyView() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()

        super.onDestroyView()
    }

    override fun layoutId() =
        resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)

    protected open fun viewModelProvider() = when (mViewModelScope) {
        SCOPE_FRAGMENT -> ViewModelProviders.of(this, mViewModelFactory)
        else -> ViewModelProviders.of(this.activity!!, mViewModelFactory)
    }.get(viewModelClass())

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

    protected open fun dialogAware() = mViewModel.run {
        if (this is IDialogAware) {
            observeDialog(dialogEvent, mDisposable)
        }
    }

    protected fun commandEventAware() = mViewModel.run {
        if (this is ICommandEventAware) {
            observe(commandEvent) {
                when (it.first) {
                    ICommandEventAware.CMD_FINISH   -> commandFinish(it.second as Boolean)
                    ICommandEventAware.CMD_TOAST    -> commandToast(it.second.toString())
                    ICommandEventAware.CMD_SNACKBAR -> commandSnackbar(it.second.toString())

                    else -> onCommandEvent(it.first, it.second)
                }
            }
        }
    }

    protected open fun commandFinish(animate: Boolean) = finish(animate)
    protected open fun commandToast(message: String) = toast(message)
    protected open fun commandSnackbar(message: String) =
        snackbar(mBinding.root, message, Snackbar.LENGTH_SHORT).show()

    protected open fun onCommandEvent(cmd: String, data: Any) { }

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

    companion object {
        const val SCOPE_ACTIVITY = 0
        const val SCOPE_FRAGMENT = 1
    }

    lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    protected lateinit var mViewModel: M

    protected var mLayoutName     = generateLayoutName()
    protected var mViewModelScope = SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDisposable = CompositeDisposable()
        mViewModel  = viewModelProvider()

        if (layoutId() == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun layoutId() =
        resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)

    protected open fun viewModelProvider() = when (mViewModelScope) {
        SCOPE_FRAGMENT -> ViewModelProviders.of(this, mViewModelFactory)
        else -> ViewModelProviders.of(this.activity!!, mViewModelFactory)
    }.get(viewModelClass())

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(viewModelClass(), mViewModel))
    }

    override fun onDestroyView() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    protected fun commandEventAware() = mViewModel.run {
        if (this is ICommandEventAware) {
            observe(commandEvent) {
                when (it.first) {
                    ICommandEventAware.CMD_FINISH -> dismiss()

                    else -> onCommandEvent(it.first, it.second)
                }
            }
        }
    }

    protected open fun onCommandEvent(cmd: String, data: Any) { }

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

    companion object {
        const val SCOPE_ACTIVITY = 0
        const val SCOPE_FRAGMENT = 1
    }

    lateinit var mDisposable: CompositeDisposable
    @Inject lateinit var mViewModelFactory: DaggerViewModelFactory
    protected lateinit var mViewModel: M

    protected var mLayoutName = generateLayoutName()
    protected var mViewModelScope = SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDisposable = CompositeDisposable()
        mViewModel  = viewModelProvider()

        if (layoutId() == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    protected open fun viewModelProvider() = when (mViewModelScope) {
        SCOPE_FRAGMENT -> ViewModelProviders.of(this, mViewModelFactory)
        else           -> ViewModelProviders.of(this.activity!!, mViewModelFactory)
    }.get(viewModelClass())

    override fun layoutId() = resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)

    private fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun viewModelMethodName() = SET_VIEW_MODEL

    override fun bindViewModel() {
        Reflect.method(mBinding, viewModelMethodName(), Reflect.Params(viewModelClass(), mViewModel))
    }

    override fun onDestroyView() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    protected fun commandEventAware() = mViewModel.run {
        if (this is ICommandEventAware) {
            observe(commandEvent) {
                when (it.first) {
                    ICommandEventAware.CMD_FINISH -> dismiss()

                    else -> onCommandEvent(it.first, it.second)
                }
            }
        }
    }

    protected open fun onCommandEvent(cmd: String, data: Any) { }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
}
