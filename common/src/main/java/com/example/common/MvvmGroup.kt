@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 10. 15. <p/>
 */

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(@StringRes resid: Int) =
    getApplication<Application>().getString(resid)

/**
 * android view model 에서 쉽게 문자열을 가져올 수 있도록 wrapping 함
 */
inline fun AndroidViewModel.string(resid: String) =
    getApplication<Application>().string(resid)

inline val AndroidViewModel.app : Application
        get() = getApplication()


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
//
//
////////////////////////////////////////////////////////////////////////////////////


interface OnBackPressedListener {
    fun onBackPressed(): Boolean
}

abstract class BaseActivity<T : ViewDataBinding> : DaggerAppCompatActivity() {
    protected lateinit var mBinding : T

    protected lateinit var mBackPressed: BackPressedManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = dataBindingView(layoutId())
        mBackPressed = BackPressedManager(this, mBinding.root)
    }

    fun <T> observe(data: LiveData<T>, observer: (T) -> Unit) {
        data.observe(this, Observer { observer(it) })
    }

    abstract fun layoutId(): Int

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
}

abstract class BaseFragment<T: ViewDataBinding> : DaggerFragment() {
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

    abstract fun layoutId(): Int
    abstract fun bindViewModel()

    fun activity() = activity as BaseActivity<out ViewDataBinding>

//    fun disposable() = if (activity != null && activity is BaseActivity<*>) {
//        activity().disposable
//    } else null
}

abstract class BaseRuleFragment<T: ViewDataBinding>: BaseFragment<T>() {
    companion object {
        protected val LAYOUT = "layout"
    }

    var className: String = ""

    fun layoutName(): String {
        if (!TextUtils.isEmpty(className)) {
            return className
        }

        val name = javaClass.simpleName
        className = name.get(0).toLowerCase().toString()

        name.substring(1, name.length).forEach {
            className += if (it.isUpperCase()) {
                "_${it.toLowerCase()}"
            } else {
                it
            }
        }

        return className
    }

    override fun layoutId() = resources.getIdentifier(layoutName(), LAYOUT, activity?.packageName)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (layoutId() == 0) {
            val view = LinearLayout(activity)
            view.lpmm()
            view.gravity = Gravity.CENTER

            val text = TextView(activity)
            text.gravityCenter()
            text.text = "FILE NOT FOUND (${layoutName()})"
            view.addView(text)

            return view
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}