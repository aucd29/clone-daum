@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import androidx.lifecycle.Observer
import brigitte.viewmodel.CommandEventViewModel
import org.mockito.Mockito
import org.mockito.verification.VerificationMode

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-23 <p/>
 */

inline fun <VM: CommandEventViewModel> Observer<Pair<String, Any>>.verifyCommandChanged(viewmodel: VM, vararg cmds: Pair<String, Any>) {
    cmds.forEach {
        viewmodel.command(it.first, it.second)
        verifyChanged(it)
    }
}

inline fun <VM: CommandEventViewModel> Observer<Pair<String, Any>>.verifyCommandChanged(viewmodel: VM, vararg cmds: String) {
    cmds.forEach {
        viewmodel.command(it)
        verifyChanged(it to -1)
    }
}

inline fun <T> Observer<T>.verifyChanged(vararg cmd: T, mode: VerificationMode = Mockito.atLeastOnce()) {
    // atLeastOnce()는 기본적으로 메소드 호출이 한 번 되는 것을 검증할 수 있다.
    cmd.forEach {
        Mockito.verify(this, mode).onChanged(it)
    }
}

inline fun Observer<Pair<String, Any>>.verifyChanged(vararg cmd: String, mode: VerificationMode = Mockito.atLeastOnce()) {
    cmd.forEach {
        Mockito.verify(this, mode).onChanged(it to -1)
    }
}

inline fun <T> Observer<T>.verifyNeverChanged(vararg cmd: T) {
    verifyChanged(*cmd, mode = Mockito.never())
}

inline fun Observer<Pair<String, Any>>.verifyNeverChanged(vararg cmd: String) {
    verifyChanged(*cmd, mode = Mockito.never())
}
