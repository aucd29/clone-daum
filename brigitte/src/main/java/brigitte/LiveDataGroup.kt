@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import brigitte.arch.SingleLiveEvent

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-03 <p/>
 */

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

