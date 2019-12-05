@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.runtimepermission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import brigitte.DialogParam
import brigitte.R
import brigitte.dialog
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 14. <p/>
 */

object RuntimePermission {
    const val REQ_MAIN = 99

    @JvmStatic
    fun check(params: PermissionParams) {
        if (!checkPermissions(params.activity, params.permissions)) {
            instanceFragment(params).apply {
                requestPermissions(params)
            }

            return
        }

        params.listener.invoke(params.reqCode, true)
    }

    @JvmStatic
    fun checkPermissions(context: Context, permissions: ArrayList<String>): Boolean {
        var result = true
        var i = 0

        while (i < permissions.size) {
            val permission = permissions[i++]
            if (!checkSelfPermission(context, permission)) {
                result = false
                break
            }
        }

        return result
    }

    private fun checkSelfPermission(context: Context, permission: String) =
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ||
                    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun instanceFragment(params: PermissionParams): PermissionFragment {
        val manager = params.activity.supportFragmentManager
        var fragment = manager.findFragmentByTag(PermissionFragment::class.java.name)

        if (fragment != null) {
            return fragment as PermissionFragment
        }

        fragment = PermissionFragment()

        // https://developer.android.com/guide/components/fragments?hl=ko
        // commitAllowingStateLoss : 커밋을 잃어버려도 상관하지 않음
        manager.beginTransaction()
            .add(fragment, PermissionFragment::class.java.name)
            .commitAllowingStateLoss()

        manager.executePendingTransactions()

        return fragment
    }
}

inline fun FragmentActivity.runtimePermissions(params: PermissionParams) {
    RuntimePermission.check(params)
}

inline fun Fragment.runtimePermissions(params: PermissionParams) {
    RuntimePermission.check(params)
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

data class PermissionParams(
    val activity    : FragmentActivity,
    val permissions : ArrayList<String>,
    val listener    : (Int, Boolean) -> Unit,
    var reqCode     : Int = RuntimePermission.REQ_MAIN
) {
    // 마치 builder pattern 처럼.. add 할 수 있도록
    fun permission(permission: String) = apply {
        permissions.add(permission)
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

open class PermissionFragment : Fragment() {
    companion object {
        private val mLog = LoggerFactory.getLogger(PermissionFragment::class.java)

        private const val REQ_SETTING_EVENT = 7911
    }

    lateinit var mParams: PermissionParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // http://aroundck.tistory.com/2163
        // true 시 생명주기를 따르지 않게되며 RECREATION 시 onCreate 없이 onAttach, onActivityCreated 순으로
        // 호출된다.
        retainInstance = true
    }

    fun requestPermissions(params: PermissionParams) {
        mParams = params

        requestPermissions(params.permissions.toTypedArray(), params.reqCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        var grantRes = true
        var i = 0

        while (i < grantResults.size) {
            val it = grantResults[i++]
            if (it == PackageManager.PERMISSION_DENIED) {
                grantRes = false
                break
            }
        }

        onPermissionResult(grantRes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_SETTING_EVENT -> {
                val result = RuntimePermission.checkPermissions(mParams.activity, mParams.permissions)
                if (result) {
                    mParams.listener.invoke(mParams.reqCode, true)
                }
            }
        }
    }

    protected fun onPermissionResult(result: Boolean) {
        if (mLog.isDebugEnabled) {
            mLog.debug("PERMISSION RESULT : $result")
        }

        if (result) {
            mParams.listener.invoke(mParams.reqCode, true)
        } else {
            confirmPermissionDialog()
        }
    }

    fun confirmPermissionDialog() {
        if (mParams.reqCode == RuntimePermission.REQ_MAIN) {
            mParams.listener.invoke(mParams.reqCode, false)
            return
        }

        dialog(DialogParam(context = requireContext()
            , messageId  = R.string.permission_message
            , titleId    = R.string.permission_title
            , positiveId = R.string.permission_set
            , negativeId = android.R.string.cancel
            , listener   = { result, dlg ->
                if (result) {
                    startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse(("package:${mParams.activity.packageName}"))
                    }, REQ_SETTING_EVENT)
                }

                mParams.listener.invoke(mParams.reqCode, result)
            }))
    }
}