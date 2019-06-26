@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import brigitte.arch.SingleLiveEvent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 10. 15. <p/>
 */

private const val SET_VIEW_MODEL  = "setModel"       // view model 을 설정하기 위한 메소드 명
private const val LAYOUT          = "layout"         // 레이아웃


/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(@StringRes resid: Int) =
    app.getString(resid)

inline fun AndroidViewModel.stringArray(@ArrayRes resid: Int) =
    app.resources.getStringArray(resid)

inline fun AndroidViewModel.intArray(@ArrayRes resid: Int) =
    app.resources.getIntArray(resid)

inline fun AndroidViewModel.requireContext() =
    if (app == null) {
        throw IllegalStateException("AndroidViewModel $this not attached to a context.")
    } else {
        app.applicationContext
    }

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(resid: String) = app.string(resid)

inline val AndroidViewModel.app : Application
        get() = getApplication()

inline fun AndroidViewModel.time() = System.currentTimeMillis()

inline fun AndroidViewModel.dpToPx(v: Int) = dpToPx(v.toFloat()).toInt()
inline fun AndroidViewModel.pxToDp(v: Int) = pxToDp(v.toFloat()).toInt()
inline fun AndroidViewModel.dpToPx(v: Float) = v * app.displayDensity()
inline fun AndroidViewModel.pxToDp(v: Float) = v / app.displayDensity()

////////////////////////////////////////////////////////////////////////////////////
//
// VIEW MODEL
//
////////////////////////////////////////////////////////////////////////////////////

inline fun <reified T : ViewModel> FragmentActivity.obtainViewModel(provider: ViewModelProvider.Factory? = null) =
    provider?.let {
        ViewModelProviders.of(this, it).get(T::class.java)
    } ?: ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.obtainViewModel(provider: ViewModelProvider.Factory? = null) =
    activity?.obtainViewModel<T>( provider)

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

// aware 에 fun 을 만들지 않다가 생각해보니 xml 에서 call 하려면 필요하다..

interface IDialogAware {
    val dialogEvent: SingleLiveEvent<DialogParam>

    fun dialog(dialog: DialogParam) {
        dialogEvent.value = dialog
    }

    fun alert(context: Context, messageId: Int, titleId: Int? = null) {
        dialog(DialogParam(context = context
            , messageId = messageId
            , titleId   = titleId))
    }

    fun confirm(context: Context, messageId: Int, titleId: Int? = null,
                listener: ((Boolean, DialogInterface) -> Unit)? = null) {
        dialog(DialogParam(context = context
            , messageId  = messageId
            , titleId    = titleId
            , negativeId = android.R.string.cancel
            , listener   = listener))
    }
}

//interface ISnackbarAware {
//    val snackbarEvent: SingleLiveEvent<String>
//
//    fun snackbar(msg: String?) {
//        msg?.let { snackbarEvent.value = it }
//    }
//
//    fun snackbar(e: Throwable) = snackbar(e.message)
//}

// xml 에서는 다음과 같이 사용할 수 있다.
// android:onClick="@{() -> model.command(model.CMD_YOUR_COMMAND)}"
interface ICommandEventAware {
    companion object {
        const val CMD_FINISH   = "cmd-finish"
        const val CMD_SNACKBAR = "cmd-snackbar"
        const val CMD_TOAST    = "cmd-toast"
    }

    val commandEvent: SingleLiveEvent<Pair<String, Any>>

    fun finish() = command(CMD_FINISH, true)
    fun finish(animate: Boolean) = command(CMD_FINISH, animate)     // XML 에서 호출해야 되서
    fun snackbar(msg: String) = command(CMD_SNACKBAR, msg)
    fun snackbar(e: Throwable) { e.message?.let { command(CMD_SNACKBAR, it) } }
    fun toast(msg: String) = command(CMD_TOAST, msg)

    // 기존에 Any? = null 형태일때 xml 에서 문제됨.
    fun command(cmd: String, data: Any) {
        commandEvent.value = cmd to data
    }

    // xml 에서 호출이 쉽게 하도록 추가
    fun command(cmd: String) {
        command(cmd, -1)
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// CommandEventViewModel
//
////////////////////////////////////////////////////////////////////////////////////

open class CommandEventViewModel(application: Application)
    : AndroidViewModel(application)
    , ICommandEventAware {

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()

    inline fun snackbar(@StringRes resid: Int) = snackbar(string(resid))
    inline fun toast(@StringRes resid: Int) = toast(string(resid))
    inline fun errorLog(e: Throwable, logger: Logger) {
        if (logger.isDebugEnabled) {
            e.printStackTrace()
        }

        logger.error("ERROR: ${e.message}")
    }
}

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
    : AppCompatActivity() {
    private var mLayoutName = generateLayoutName()

    protected lateinit var mBinding: T
    protected val mViewModel: M by lazy { initViewModel() }
    protected val mDisposable: CompositeDisposable by lazy { initDisposable() }
    protected val mBackPressed: BackPressedManager by lazy {
        BackPressedManager(this, mBinding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = dataBindingView(resources.getIdentifier(mLayoutName, LAYOUT, packageName))

        bindViewModel()
        dialogAware()
        commandEventAware()

        initViewBinding()
        initViewModelEvents()
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

    /**
     * 앱 종료 시 CompositeDisposable 를 clear 한다.
     */
    override fun onDestroy() {
        // https://stackoverflow.com/questions/47057885/when-to-call-dispose-and-clear-on-compositedisposable
        // 이건 좀 그렇다 =_ = 안쓰면 안나오는데 굳이 이걸 호출해서 instance 하는 형태니 =_ = ㅋㅋㅋㅋㅋ
        mDisposable.dispose()

        super.onDestroy()
    }

    fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    /**
     * brigitte.viewModel 을 ViewDataBinding 에 설정 한다.
     */
    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))
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
    abstract fun initDisposable(): CompositeDisposable
    abstract fun initViewModel(): M

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        const val SCOPE_ACTIVITY = 0
        const val SCOPE_FRAGMENT = 1
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseFragment<T: ViewDataBinding, M: ViewModel>
    : Fragment() {
    private var mLayoutName = generateLayoutName()

    protected lateinit var mBinding : T
    protected val mViewModel: M by lazy { initViewModel() }
    protected val mDisposable = CompositeDisposable()
    protected var mViewModelScope = SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutId = resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)
        if (layoutId == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        mBinding = dataBinding(layoutId, container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
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

    protected fun viewModelClass() = Reflect.classType(this, 1) as Class<M>

    protected open fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))
    }

    protected fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    protected fun activity()  = requireActivity()
    protected inline fun <reified F: Fragment> find() =
        fragmentManager?.find<F>()

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

    protected abstract fun initViewBinding()
    protected abstract fun initViewModelEvents()
    protected abstract fun initViewModel(): M

    companion object {
        const val SCOPE_ACTIVITY = 0
        const val SCOPE_FRAGMENT = 1
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseDialogFragment<T: ViewDataBinding, M: ViewModel>
    : AppCompatDialogFragment() {
    private var mLayoutName = generateLayoutName()

    protected lateinit var mBinding : T
    protected val mDisposable = CompositeDisposable()
    protected val mViewModel: M by lazy { initViewModel() }
    protected var mViewModelScope = BaseFragment.SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layoutId = resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)
        if (layoutId == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        mBinding = dataBinding(layoutId, container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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

    protected fun bindViewModel() {
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))
    }

    protected fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    protected fun activity()  = requireActivity()

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
    abstract fun initViewModel(): M
}

////////////////////////////////////////////////////////////////////////////////////
//
// BaseBottomSheetDialogFragment
//
////////////////////////////////////////////////////////////////////////////////////

abstract class BaseBottomSheetDialogFragment<T: ViewDataBinding, M: ViewModel>
    : BottomSheetDialogFragment() {
    private var mLayoutName = generateLayoutName()

    protected lateinit var mBinding : T
    protected val mDisposable = CompositeDisposable()
    protected val mViewModel: M by lazy { initViewModel() }
    protected var mViewModelScope = BaseFragment.SCOPE_FRAGMENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutId = resources.getIdentifier(mLayoutName, LAYOUT, activity?.packageName)
        if (layoutId == 0) {
            return generateEmptyLayout(mLayoutName)
        }

        mBinding = dataBinding(layoutId, container, false)
        mBinding.root.isClickable = true

        bindViewModel()
        stateCallback()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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

    protected fun bindViewModel() {
//        mBinding.setVariable(BR.setModel, mViewModel)
        Reflect.method(mBinding, SET_VIEW_MODEL, Reflect.Params(viewModelClass(), mViewModel))
    }

    protected fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    protected fun activity()  = requireActivity()

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
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    abstract fun initViewBinding()
    abstract fun initViewModelEvents()
    abstract fun initViewModel(): M

    companion object {
        private val mLog = LoggerFactory.getLogger(BaseBottomSheetDialogFragment::class.java)
    }
}
