package brigitte.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleEventObserver
import brigitte.ICommandEventAware
import brigitte.arch.SingleLiveEvent
import org.slf4j.Logger

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-21 <p/>
 */

open class CommandEventViewModel @JvmOverloads constructor (
    app: Application
) : AndroidViewModel(app), ICommandEventAware {

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

abstract class LifeCycleCommandEventViewModel @JvmOverloads constructor (
    app: Application
) : CommandEventViewModel(app), LifecycleEventObserver