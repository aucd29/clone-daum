@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.viewpager.widget.PagerAdapter
import brigitte.viewmodel.CommandEventViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 10. 15. <p/>
 */

private const val SET_VIEW_MODEL  = "setModel"       // view model 을 설정하기 위한 메소드 명
private const val LAYOUT          = "layout"         // 레이아웃

const val SCOPE_ACTIVITY = 0
const val SCOPE_FRAGMENT = 1

@IntDef(value = [SCOPE_ACTIVITY, SCOPE_FRAGMENT])
annotation class ViewModelScope

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

inline fun <T: ViewDataBinding> PagerAdapter.dataBinding(@LayoutRes resid: Int, parent: ViewGroup? = null,
                                                         attachToParent: Boolean = false): T =
    DataBindingUtil.inflate(LayoutInflater.from(parent?.context), resid, parent, attachToParent)


////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

interface OnBackPressedListener {
    fun onBackPressed(): Boolean
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseActivity
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseActivity<T : ViewDataBinding, M: ViewModel>
    : AppCompatActivity(), BaseEventAware {
    companion object {
        const val SCOPE_ACTIVITY = 0
        const val SCOPE_FRAGMENT = 1
    }

    private val mDisposable = CompositeDisposable()
    private lateinit var mBackPressed: BackPressedManager

    @get:LayoutRes
    protected abstract val layoutId: Int
    protected lateinit var mBinding: T

    protected val mViewModelClass = Reflect.classType<Class<M>>(this, 1)
    protected val mViewModel: M by lazy(LazyThreadSafetyMode.NONE) { initViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = dataBindingView(layoutId)
        initBackPressed()
        bindViewModel()

        addCommandEventModel(mViewModel)
        dialogAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

//    override fun onBackPressed() {
//        // 현재의 문제점 부분
//        // callback 을 등록했다는 의미는 callback 을 상위에서 쓰겠다는 의미이고
//        // 안쓰면 그 이후 처리를 해야 되는데
//        // 지금 구조는 무조건 적으로 onBackPressed 가 호출되어야지만
//        // 되는 거라 =_ = 이상함 다른 방법이 있나? [aucd29][2019-09-05]
//        if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
//            mBackPressed.onBackPressed()
//            return
//        }
//
//        super.onBackPressed()
//    }

    override fun onBackPressed() {
        // 현재 fragment 가 OnBackPressedListener 를 상속 받고 return true 를 하면 인터페이스에서
        // h/w backkey 를 처리한 것으로 본다.
        val fragment = supportFragmentManager.current
        if (fragment != null && fragment is OnBackPressedListener && fragment.onBackPressed()) {
            return
        }

        if (mBackPressed.onBackPressed()) {
            super.onBackPressed()
        }
    }

    /**
     * 앱 종료 시 CompositeDisposable 를 clear 한다.
     */
    override fun onDestroy() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()
        removeCommandEventObservers(this@BaseActivity)

        super.onDestroy()
    }

    /**
     * brigitte.viewModel 을 ViewDataBinding 에 설정 한다.
     */
    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(mViewModelClass, mViewModel))

        // live data 를 xml 에서 data binding 하기 위해서는 lifecycleOwner 를 등록해야 함
        mBinding.lifecycleOwner = this
    }

    protected open fun initBackPressed() {
        mBackPressed = BackPressedManager(this, mBinding.root)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
    abstract fun initViewModel(): M

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // BaseEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override val mCommandEventModels: ArrayList<ICommandEventAware> = arrayListOf()
    override fun disposable() = mDisposable
    override fun activity() = this@BaseActivity
    override fun rootView() = mBinding.root
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseFragment<T: ViewDataBinding, M: ViewModel>
    : Fragment(), BaseEventAware {
    private val mDisposable = CompositeDisposable()

    protected abstract val layoutId: Int
    protected lateinit var mBinding : T

    @ViewModelScope
    protected var mViewModelScope = SCOPE_FRAGMENT
    protected val mViewModelClass = Reflect.classType<Class<M>>(this, 1)
    protected val mViewModel: M by lazy(LazyThreadSafetyMode.NONE) { initViewModel() }
    protected var mStateViewModelFactory: AbstractSavedStateViewModelFactory? = null

//    private var mOnBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId, container)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        initBackPressedDispatcher()
        super.onActivityCreated(savedInstanceState)

        addCommandEventModel(mViewModel)
        dialogAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun onDestroyView() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()
        removeCommandEventObservers(this@BaseFragment)

        super.onDestroyView()
    }

    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(mViewModelClass, mViewModel))

        // live data 를 xml 에서 data binding 하기 위해서는 lifecycleOwner 를 등록해야 함
        mBinding.lifecycleOwner = this
    }

//    private fun initBackPressedDispatcher() {
//        if (this is OnBackPressedListener) {
//            mOnBackPressedCallback = object: OnBackPressedCallback(false) {
//                override fun handleOnBackPressed() {
//                    mOnBackPressedCallback?.isEnabled = !onBackPressed()
//                }
//            }
//
//            mOnBackPressedCallback?.let {
//                requireActivity().onBackPressedDispatcher.addCallback(this@BaseFragment, it)
//            }
//        }
//    }

//    protected fun backPressedCallback(enableCallback: Boolean = true) {
//        mOnBackPressedCallback?.isEnabled = enableCallback
//    }

//    ////////////////////////////////////////////////////////////////////////////////////
//    //
//    // NAVIGATE
//    //
//    ////////////////////////////////////////////////////////////////////////////////////
//
//    protected inline fun navigate(@IdRes actionId: Int, bundle: Bundle? = null) {
//        mBinding.root.findNavController().navigate(actionId, bundle)
//    }
//
//    protected inline fun navigate(view: View, actionId: Int, bundle: Bundle? = null) {
//        Navigation.findNavController(view).navigate(actionId, bundle)
//    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    protected abstract fun initViewBinding()
    protected abstract fun initViewModelEvents()
    protected abstract fun initViewModel(): M

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // BaseEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override val mCommandEventModels: ArrayList<ICommandEventAware> = arrayListOf()
    override fun disposable() = mDisposable
    override fun activity() = requireActivity()
    override fun rootView() = mBinding.root
    override fun commandFinish() = finish()
    override fun lifecycle() = lifecycle
    override fun lifecycleOwner() = this@BaseFragment
}


////////////////////////////////////////////////////////////////////////////////////
//
// BaseDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////


abstract class BaseDialogFragment<T: ViewDataBinding, M: ViewModel> constructor()
    : AppCompatDialogFragment(), BaseEventAware {
    private val mDisposable = CompositeDisposable()

    @get:LayoutRes
    protected abstract val layoutId: Int
    protected lateinit var mBinding : T

    @ViewModelScope
    protected var mViewModelScope = SCOPE_FRAGMENT
    protected val mViewModelClass = Reflect.classType<Class<M>>(this, 1)
    protected val mViewModel: M by lazy(LazyThreadSafetyMode.NONE) { initViewModel() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId, container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addCommandEventModel(mViewModel)
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun onDestroyView() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()
        removeCommandEventObservers(this@BaseDialogFragment)

        super.onDestroyView()
    }

    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(mViewModelClass, mViewModel))

        // live data 를 xml 에서 data binding 하기 위해서는 lifecycleOwner 를 등록해야 함
        mBinding.lifecycleOwner = this
    }

//    ////////////////////////////////////////////////////////////////////////////////////
//    //
//    // NAVIGATE
//    //
//    ////////////////////////////////////////////////////////////////////////////////////
//
//    protected inline fun navigate(@IdRes actionId: Int, bundle: Bundle? = null) {
//        mBinding.root.findNavController().navigate(actionId, bundle)
//    }
//
//    protected inline fun navigate(view: View, actionId: Int, bundle: Bundle? = null) {
//        Navigation.findNavController(view).navigate(actionId, bundle)
//    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ABSTRACT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
    abstract fun initViewModel(): M

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // BaseEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override val mCommandEventModels: ArrayList<ICommandEventAware> = arrayListOf()
    override fun disposable() = mDisposable
    override fun activity() = requireActivity()
    override fun rootView() = mBinding.root
    override fun commandFinish() = dismiss()
    override fun lifecycle() = lifecycle
    override fun lifecycleOwner() = this@BaseDialogFragment
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel> constructor()
    : BottomSheetDialogFragment(), BaseEventAware {

    companion object {
        private val mLog = LoggerFactory.getLogger(BaseBottomSheetDialogFragment::class.java)
    }

    protected val mDisposable = CompositeDisposable()

    @get:LayoutRes
    protected abstract val layoutId: Int
    protected lateinit var mBinding : T

    @ViewModelScope
    protected var mViewModelScope = SCOPE_FRAGMENT
    protected val mViewModelClass = Reflect.classType<Class<M>>(this, 1)
    protected val mViewModel: M by lazy { initViewModel() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId, container, false)
        mBinding.root.isClickable = true

        bindViewModel()
        stateCallback()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addCommandEventModel(mViewModel)
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateCallback()
    }

    override fun onDestroyView() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        mDisposable.dispose()
        removeCommandEventObservers(this@BaseBottomSheetDialogFragment)

        super.onDestroyView()
    }

    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(mViewModelClass, mViewModel))

        // live data 를 xml 에서 data binding 하기 위해서는 lifecycleOwner 를 등록해야 함
        mBinding.lifecycleOwner = this
    }

    private fun stateCallback() {
        mBinding.root.globalLayoutListener {
            BottomSheetBehavior.from(mBinding.root.parent as View).apply {
                setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(p0: View, p1: Float) {}

                    @SuppressLint("SwitchIntDef")
                    override fun onStateChanged(view: View, state: Int) {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("STATE CHANGED $state")
                        }

                        when (state) {
                            BottomSheetBehavior.STATE_COLLAPSED,
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("STATE HIDDEN | STATE_COLLAPSED")
                                }

                                dismiss()
                            }
                        }
                    }
                })
            }

            true
        }
    }

    // https://stackoverflow.com/questions/45614271/bottomsheetdialogfragment-doesnt-show-full-height-in-landscape-mode
    fun wrapContentHeight() {
        mBinding.root.globalLayoutListener {
            BottomSheetBehavior.from(mBinding.root.parent as View).apply {
                state      = BottomSheetBehavior.STATE_EXPANDED
                peekHeight = 0
            }

            true
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
    abstract fun initViewModel(): M

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // BaseEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override val mCommandEventModels: ArrayList<ICommandEventAware> = arrayListOf()
    override fun disposable() = mDisposable
    override fun activity() = requireActivity()
    override fun rootView() = mBinding.root
    override fun lifecycle() = lifecycle
    override fun lifecycleOwner() = this@BaseBottomSheetDialogFragment
}

/**
 * 공통 부분이 많아 인터페이스로 빼고 이를 이용하도록 수정
 */
interface BaseEventAware {
    val mCommandEventModels: ArrayList<ICommandEventAware>

    fun disposable(): CompositeDisposable
    fun activity(): FragmentActivity
    fun rootView(): View

    fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(lifecycleOwner(), Observer { observer(it) })
    }

    fun <T> editPreference(data: LiveData<T>, key: String, observer: ((T) -> Unit)?) {
        if (data.hasObservers()) {
            return
        }

        data.observe(lifecycleOwner(), Observer {
            activity().prefs().edit {
                when (it) {
                    is String  -> putString(key, it)
                    is Int     -> putInt(key, it)
                    is Boolean -> putBoolean(key, it)
                    is Float   -> putFloat(key, it)
                    is Long    -> putLong(key, it)
                }

                observer?.invoke(it)
            }
        })
    }

    /**
     * view model 에 등록되어 있는 dialog live event 의 값에 변화를 감지하여 이벤트를 발생 시킨다.
     */
    fun dialogAware() {
        mCommandEventModels.forEach { vm ->
            if (vm is IDialogAware) {
                activity().observeDialog(vm.dialogEvent, disposable())
            }
        }
    }

    /**
     * view model 에 등록되어 있는 command live event 의 값에 변화를 감지하여 이벤트를 발생 시킨다.
     */
    fun commandEventAware() {
        mCommandEventModels.forEach { vm ->
            observe(vm.commandEvent) {
                when (it.first) {
                    ICommandEventAware.CMD_FINISH   -> commandFinish()
                    ICommandEventAware.CMD_TOAST    -> commandToast(it.second.toString())
                    ICommandEventAware.CMD_SNACKBAR -> commandSnackbar(it.second.toString())

                    else -> onCommandEvent(it.first, it.second)
                }
            }
        }
    }

    fun addCommandEventModel(viewmodel: ViewModel) {
        if (viewmodel is ICommandEventAware) { mCommandEventModels.add(viewmodel) }
        if (viewmodel is LifecycleObserver) {
            lifecycle().addObserver(viewmodel)
        }
    }

    fun addCommandEventModels(vararg viewmodels: ViewModel) {
        viewmodels.forEach(::addCommandEventModel)
    }

    fun removeCommandEventModel(viewmodel: ViewModel) {
        if (viewmodel is ICommandEventAware) {
            mCommandEventModels.remove(viewmodel)
        }
        if (viewmodel is LifecycleObserver) {
            lifecycle().removeObserver(viewmodel)
        }
    }

    fun removeCommandEventModels(vararg viewmodels: ViewModel) {
        viewmodels.forEach(::removeCommandEventModel)
    }

    fun removeCommandEventObservers(owner: LifecycleOwner) {
        mCommandEventModels.forEach {
            if (it is CommandEventViewModel) {
                it.commandEvent.removeObservers(owner)
            }
        }
        mCommandEventModels.clear()
    }

    fun onCommandEvent(cmd: String, data: Any) { }
    fun commandFinish() = activity().finish()
    fun commandToast(message: String) = activity().toast(message)
    fun commandSnackbar(message: String) =
        activity().snackbar(rootView(), message).show()
    fun lifecycle() = activity().lifecycle
    fun lifecycleOwner(): LifecycleOwner = activity()
}
