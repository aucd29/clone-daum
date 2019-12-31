@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import brigitte.arch.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.Logger

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 8. <p/>
 */

/** dp 값을 px 로 변환 */
inline fun Fragment.dpToPx(v: Float) = v * requireContext().displayDensity()
/** px 값을 dp 로 변환 */
inline fun Fragment.pxToDp(v: Float) = v / requireContext().displayDensity()
/** dp 값을 px 로 변환 */
inline fun Fragment.dpToPx(v: Int) = dpToPx(v.toFloat()).toInt()
/** px 값을 dp 로 변환 */
inline fun Fragment.pxToDp(v: Int) = pxToDp(v.toFloat()).toInt()

/** 문자열 리소스에 해당하는 문자열을 반환 */
inline fun Fragment.string(@StringRes resid: Int): String = requireContext().string(resid)
inline fun Fragment.stringArray(@ArrayRes resid: Int) = requireContext().stringArray(resid)
inline fun Fragment.intArray(@ArrayRes resid: Int) = requireContext().intArray(resid)
inline fun Fragment.color(@ColorRes resid: Int) =
    ContextCompat.getColor(requireContext(), resid)

/** 현재 화면에 위치하는 Fragment 를 반환 */
inline val FragmentManager.current: Fragment?
    get() = lastBaseFragment()

inline fun FragmentManager.lastBaseFragment(): Fragment? {
    val it = fragments.listIterator(fragments.size)

    while (it.hasPrevious()) {
        val f = it.previous()
        if (f is BaseFragment<*,*>) {
            return f
        }
    }

    return null
}

/** 현재 등록되어 있는 Fragment 개수를 반환 */
inline val FragmentManager.count: Int
    get() = backStackEntryCount

/** 등록되어 있는 Fragment 내에서 원하는 Fragment 를 검색해서 반환 */
inline fun <reified T: Fragment> FragmentManager.find() =
    findFragmentByTag(T::class.java.name) as T?

// FIXME 기존에 fragmentManager 가 depreacated 됨에 따라 이를 parentFragmentManager 로 변경 한다. [aucd29][2019-12-05]
inline fun <reified F: Fragment> Fragment.find() =
    parentFragmentManager.find<F>()

/**
 * 다이얼로그를 띄우기 위한 옵저버 로 view model 에 선언되어 있는 single live event 의 값의 변화를 인지 하여 dialog 을 띄운다.
 */
inline fun Fragment.observeDialog(event: SingleLiveEvent<DialogParam>, disposable: CompositeDisposable? = null) {
    event.observe(this, Observer { dialog(it, disposable) })
}

/**
 * snackbar 호출
 */
inline fun Fragment.snackbar(view: View, msg: String, length: Int = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT) =
    requireActivity().snackbar(view, msg, length)

/**
 * snackbar 호출
 */
inline fun Fragment.snackbar(view: View, @StringRes resid: Int, length: Int = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT) =
    requireActivity().snackbar(view, resid, length)

/**
 * dialog 을 띄운다.
 */
inline fun Fragment.dialog(params: DialogParam, disposable: CompositeDisposable? = null) {
    requireActivity().dialog(params, disposable)
}

inline fun Fragment.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    requireActivity().toast(msg, length)
}

inline fun Fragment.toast(@StringRes resid: Int, length:Int = Toast.LENGTH_SHORT) {
    requireActivity().toast(resid, length)
}

inline fun Fragment.errorLog(e: Throwable, logger: Logger) {
    activity?.errorLog(e, logger)
}

/**
 * alert 형태의 다이얼로그를 띄운다.
 */
inline fun Fragment.alert(messageId: Int, titleId: Int? = null,
                          noinline listener: ((Boolean, DialogInterface) -> Unit)? = null) {
    dialog(DialogParam(context = requireContext()
        , messageId = messageId
        , titleId   = titleId
        , listener  = listener))
}

/**
 * confirm 형태의 다이얼로그를 띄운다.
 */
inline fun Fragment.confirm(messageId: Int, titleId: Int? = null,
                            noinline listener: ((Boolean, DialogInterface) -> Unit)? = null) {
    dialog(DialogParam(context = requireContext()
        , messageId  = messageId
        , titleId    = titleId
        , negativeId = android.R.string.cancel
        , listener   = listener))
}

/**
 * vk 를 숨긴다.
 */
inline fun Fragment.hideKeyboard(view: View) =
    activity?.hideKeyboard(view)

/**
 * fragment 를 종료 시키낟.
 */
inline fun Fragment.finish(animate: Boolean = true) {
    // FIXME 기존에 fragmentManager 가 depreacated 됨에 따라 이를 parentFragmentManager 로 변경 한다. [aucd29][2019-12-05]
    parentFragmentManager.run {
        if (!animate) {
            beginTransaction().setCustomAnimations(0, 0).commit()
        }

        pop()
    } ?: Log.e("[BK]", "Fragment.finish() fragment manager == null")

    // fixme fragmentManager 가 deprecated 되서 == 을 반환 함  삭제 함 [aucd29][2019-11-11]
//    if (animate) {
//        fragmentManager?.pop()
//    } else {
//        fragmentManager?.apply {
//            beginTransaction().setCustomAnimations(0, 0).commit()
//            pop()
//        }
//    }

//    if (!animate) {
//        fragmentManager?.beginTransaction()?.setCustomAnimations(0, 0)?.commitNow()
//    }
//
//    findNavController().popBackStack()
}

// FIXME 기존에 fragmentManager 가 depreacated 됨에 따라 이를 parentFragmentManager 로 변경 한다. [aucd29][2019-12-05]
inline fun Fragment.finishInclusive() =
    parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

/**
 * 화면을 ON / OFF 시킨다.
 *
 * @param on true일 경우 화면을 켜고 아닐경우 끈다.
 */
inline fun Fragment.keepScreen(on: Boolean) {
    activity?.keepScreen(on)
}

/**
 * fragment 를 add 또는 replace 할 때 화면에 나타내야할 animation 효과를 지정한다.
 */
object FragmentAnim {
    val LEFT: String
        get() = "left"

    val RIGHT: String
        get() = "right"

    val RIGHT_ALPHA: String
        get() = "right-alpha"

    val UP: String
        get() = "up"

    val ALPHA: String
        get() = "alpha"
}

/**
 * fragment 를 add 또는 replace 할 때 commit 에 옵션
 */
object FragmentCommit {
    /** 동기적으로 트랜잭션 처리 */
    val NOW: String
        get() = "now"

    /**
     * 커밋과 유사하지만  activity 의 상태 값이 저장된 이후 실행 된다.
     * 나중에 작업을 해당 상태에서 복원해야하는 경우 커밋이 손실 될 수 있어
     * 위험하므로 UI 상태가 예기치 않게 변경되는 경우에만 사용해야함
     */
    val ALLOW_STATE: String
        get() = "allow"

    /**
     * commitNow ()와 같지만 활동의 상태가 저장된 후에 커밋이 실행될 수 있음
     * 현재 상태를 복원해야하는 경우 커밋이 손실 될 수 있어
     * 위험하므로 UI 상태가 예기치 않게 변경되는 경우에만 사용해야함
     */
    val NOW_ALLOW_STATE: String
        get() = "notallow"
}

inline fun FragmentManager.showBy(params: FragmentParams) {
    val fragment = params.fragment
    if (fragment != null && fragment.isVisible) {
        // manager 내에 해당 fragment 가 이미 존재하면 해당 fragment 를 반환 한다
        return
    }

    fragment?.let { internalShow(it, params) }
}

inline fun <reified T: Fragment> FragmentManager.show(params: FragmentParams): Fragment? {
    val existFragment = findFragmentByTag(T::class.java.name)
    if (existFragment != null && existFragment.isVisible) {
        // manager 내에 해당 fragment 가 이미 존재하면 해당 fragment 를 반환 한다
        return existFragment
    }

    val fragment =  T::class.java.newInstance() as Fragment
    internalShow(fragment, params)

    return fragment
}

inline fun FragmentManager.internalShow(fragment: Fragment, params: FragmentParams) {
    val transaction = beginTransaction()

    params.apply {
        bundle?.let { fragment.arguments = it }

        transaction.apply {
            anim?.let {
                FragmentAnim.apply {
                    when (it) {
                        RIGHT -> setCustomAnimations(R.anim.slide_in_current,   R.anim.slide_in_next,
                                                     R.anim.slide_out_current,  R.anim.slide_out_prev)
                        RIGHT_ALPHA -> setCustomAnimations(R.anim.slide_in_current_fadein,   R.anim.slide_in_next_fadeout,
                                                           R.anim.slide_out_current_fadein,  R.anim.slide_out_prev_fadeout)
                        LEFT  -> setCustomAnimations(R.anim.slide_out_current,  R.anim.slide_out_prev,
                                                     R.anim.slide_in_current,   R.anim.slide_in_next)
                        UP    -> setCustomAnimations(R.anim.slide_up_current,   R.anim.slide_up_next,
                                                     R.anim.slide_down_current, R.anim.slide_down_prev)
                        ALPHA -> setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)

                        else -> { }
                    }
                }
            }

            // findFragmentByTag 를 위해 fragment.javaClass.name 를 등록
            if (add) {
                add(containerId, fragment, fragment.javaClass.name)
            } else {
                replace(containerId, fragment, fragment.javaClass.name)
            }

            if (backStack) {
                addToBackStack(fragment.javaClass.name)
            }

            commit?.let {
                FragmentCommit.apply {
                    when (it) {
                        NOW             -> commitNow()
                        ALLOW_STATE     -> commitAllowingStateLoss()
                        NOW_ALLOW_STATE -> commitNowAllowingStateLoss()
                        else            -> commit()
                    }
                }
            } ?: commit()
        }
    }
}

/**
 * Fragment 를 삭제 한다.
 */
inline fun <reified T: Fragment> FragmentManager.remove() {
    findFragmentByTag(T::class.java.name)?.let {
        beginTransaction().remove(it).commit()
    } ?: throw AssertionError("Fragment Not Found : ${T::class.java.name}")
}

/**
 * Fragment 를 pop 한다 (back pressed)
 */
inline fun FragmentManager.pop() {
    popBackStack()
}

/**
 * DialogFragment 를 show 한다.
 */
inline fun FragmentManager.showDialog(frgmt: DialogFragment, name: String) {
    val transaction = beginTransaction()
    findFragmentByTag(name)?.let { transaction.remove(it) }
    frgmt.show(transaction, name)
}

/**
 * 모든 Fragment 를 pop 한다.
 */
inline fun FragmentManager.popAll() {
    (0 until count).map { popBackStack(it, FragmentManager.POP_BACK_STACK_INCLUSIVE) }
}

/**
 * Fragment 를 띄우기 위한 인자 값들
 */
data class FragmentParams(
    /** add 또는 replace 에 대상이 되는 view 의 id */
    @IdRes val containerId: Int,
    /** dagger 나 직접 instance 를 이용시 fragment 객체 */
    val fragment: Fragment? = null,
    /** true 면 add 시키고 false 면 replace 한다. add 면 기존에 Fragment 를 나두고 추가 시키고 replace 면 기존에 걸 교체 한다.  */
    val add: Boolean = true,
    /** 트렌젝션 시 화면에 나타나야할 animation 종류 */
    var anim: String? = null,
    /** Fragment 에 전달해야할 인자 값들 Fragment 는 arguments 를 통해 이 값을 얻을 수 있다. */
    var bundle: Bundle? = null,
    /** 커밋 형태  */
    var commit: String? = null,
    /** back stack 추가 유/무 */
    var backStack: Boolean = true
)

/**
 * Fragment 클래스 명을 기반하여 inflate 할 layout 이름을 얻도록 한다. 이때 첫글자 뒤에 오는 대문자는 _ 을 붙이고 대문자는 소문자로 변환 된다.
 * 예로 TestFragment 라면 test_fragment 형태로 변환 된다.
 */
inline fun Fragment.generateLayoutName(): String {
    val name = javaClass.simpleName
    var layoutName = name[0].toLowerCase().toString()

    name.substring(1, name.length).forEach {
        layoutName += if (it.isUpperCase()) {
            "_${it.toLowerCase()}"
        } else {
            it
        }
    }

    return layoutName
}

/**
 * generateLayoutName 로 얻은 layout xml 이 없을 경우 화면에 경고 문구를 출력하기 위해 view 를 생성해서 전달 한다.
 */
@SuppressLint("SetTextI18n")
inline fun Fragment.generateEmptyLayout(name: String): LinearLayout {
    val view = LinearLayout(activity)
    view.lpmm()
    view.gravity = Gravity.CENTER

    val text = TextView(activity)
    text.gravityCenter()
    text.text = "FILE NOT FOUND ($name)"
    view.addView(text)

    return view
}
