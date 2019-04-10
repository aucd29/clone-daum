@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.*
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 10. 15. <p/>
 */

private const val LAYOUT = "layout"

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(@StringRes resid: Int) =
    app.getString(resid)

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

abstract class BaseFragment<T: ViewDataBinding> : DaggerFragment() {
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

    protected fun activity()  = requireActivity()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()

        return mBinding.root
    }

    fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    protected fun activity()  = requireActivity()

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
    companion object {
        private val mLog = LoggerFactory.getLogger(BaseBottomSheetDialogFragment::class.java)
    }

    protected lateinit var mBinding : T

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = dataBinding(layoutId(), container, false)
        mBinding.root.isClickable = true

        bindViewModel()
        stateCallback()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateCallback()
    }

    fun stateCallback() {
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

    abstract fun layoutId(): Int
    abstract fun bindViewModel()
}
