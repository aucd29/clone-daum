@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.viewpager.widget.PagerAdapter
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

interface BaseViewModelProvider {
    val mViewModelProvider: ViewModelProvider
    val mViewModelProviderActivity: ViewModelProvider

    private fun viewModelProvider(@ViewModelScope scope: Int) =
        if (scope == SCOPE_FRAGMENT) mViewModelProvider else mViewModelProviderActivity

    private fun scope(some: FragmentActivity?) =
        if (some == null) SCOPE_FRAGMENT else SCOPE_ACTIVITY

    fun <VM : ViewModel> inject(@ViewModelScope scope: Int, klass: Class<VM>) =
        viewModelProvider(scope).get(klass)

    fun <VM : ViewModel> inject(activity: FragmentActivity?, klass: Class<VM>) =
        viewModelProvider(scope(activity)).get(klass)
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseActivity
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseActivity<T : ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : AppCompatActivity(), BaseEventAware {
    companion object {
        const val SCOPE_ACTIVITY = 0
        const val SCOPE_FRAGMENT = 1
    }

    private val mDisposable = CompositeDisposable()
    private lateinit var mBackPressed: BackPressedManager

    protected val mViewModel: M by lazy { initViewModel() }
    protected lateinit var mBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = dataBindingView(layoutId())

        initBackPressed()
        bindViewModel()

        addCommandEventModel(mViewModel)
        dialogAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
    }

    // 리소스 사용 확인 문제로 이를 LiveTemplate 에서 처리 하도록 수정
    @LayoutRes
    abstract fun layoutId(): Int

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
        mCommandEventModels.clear()

        super.onDestroy()
    }

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    /**
     * brigitte.viewModel 을 ViewDataBinding 에 설정 한다.
     */
    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))

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
    override fun activity() = this
    override fun rootView() = mBinding.root
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : Fragment(), BaseEventAware {

    protected lateinit var mBinding : T
    protected val mViewModel: M by lazy { initViewModel() }
    protected val mDisposable = CompositeDisposable()

    @ViewModelScope
    protected var mViewModelScope = SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    // 리소스 사용 확인 문제로 이를 LiveTemplate 에서 처리 하도록 수정
    @LayoutRes
    abstract fun layoutId(): Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
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
        mCommandEventModels.clear()

        super.onDestroyView()
    }

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))

        // live data 를 xml 에서 data binding 하기 위해서는 lifecycleOwner 를 등록해야 함
        mBinding.lifecycleOwner = this
    }

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
}


////////////////////////////////////////////////////////////////////////////////////
//
// BaseDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////


abstract class BaseDialogFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : AppCompatDialogFragment(), BaseEventAware {
    protected lateinit var mBinding : T
    protected val mDisposable = CompositeDisposable()
    protected val mViewModel: M by lazy { initViewModel() }

    @ViewModelScope
    protected var mViewModelScope = SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    // 리소스 사용 확인 문제로 이를 LiveTemplate 에서 처리 하도록 수정
    @LayoutRes
    abstract fun layoutId(): Int // = resources.getIdentifier(mLayoutName, LAYOUT, requireActivity().packageName)

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

        super.onDestroyView()
    }

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    open protected fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))

        // live data 를 xml 에서 data binding 하기 위해서는 lifecycleOwner 를 등록해야 함
        mBinding.lifecycleOwner = this
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
    override fun activity() = requireActivity()
    override fun rootView() = mBinding.root
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel> @JvmOverloads constructor()
    : BottomSheetDialogFragment(), BaseEventAware {

    companion object {
        private val mLog = LoggerFactory.getLogger(BaseBottomSheetDialogFragment::class.java)
    }

    protected lateinit var mBinding : T
    protected val mDisposable = CompositeDisposable()
    protected val mViewModel: M by lazy { initViewModel() }

    @ViewModelScope
    protected var mViewModelScope = SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()
        stateCallback()

        return mBinding.root
    }

    // 리소스 사용 확인 문제로 이를 LiveTemplate 에서 처리 하도록 수정
    @LayoutRes
    abstract fun layoutId(): Int // = resources.getIdentifier(mLayoutName, LAYOUT, requireActivity().packageName)

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

        super.onDestroyView()
    }

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))

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
        data.observe(activity(), Observer { observer(it) })
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
            if (vm is ICommandEventAware) {
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
    }

    fun addCommandEventModel(viewmodel: ViewModel) {
        if (viewmodel is ICommandEventAware) { mCommandEventModels.add(viewmodel) }
        if (viewmodel is LifecycleObserver) {
            activity().lifecycle.addObserver(viewmodel)
        }
    }

    fun addCommandEventModels(vararg viewmodels: ViewModel) {
        for (viewModel in viewmodels) {
            addCommandEventModel(viewModel)
        }
    }

    fun removeCommandEventModel(viewmodel: ViewModel) {
        if (viewmodel is ICommandEventAware) { mCommandEventModels.remove(viewmodel) }
        if (viewmodel is LifecycleObserver) {
            activity().lifecycle.removeObserver(viewmodel)
        }
    }

    fun removeCommandEventModels(vararg viewmodels: ViewModel) {
        for (viewModel in viewmodels) {
            removeCommandEventModel(viewModel)
        }
    }

    fun onCommandEvent(cmd: String, data: Any) { }
    fun commandFinish() = activity().finish()
    fun commandToast(message: String) = activity().toast(message)
    fun commandSnackbar(message: String) =
        activity().snackbar(rootView(), message).show()
}
