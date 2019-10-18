package com.example.clone_daum.ui.main.navigation

import android.app.Application
import androidx.databinding.ObservableFloat
import brigitte.DialogParam
import brigitte.IDialogAware
import brigitte.arch.SingleLiveEvent
import brigitte.viewmodel.CommandEventViewModel
import brigitte.viewmodel.app
import com.example.clone_daum.R
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 20. <p/>
 */

class NavigationViewModel @Inject constructor(
    application: Application
) : CommandEventViewModel(application), IDialogAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationViewModel::class.java)

        const val URL_NOTIFICATION    = "https://m.daum.net/channel/notice_an"

        const val CMD_SETTING         = "setting"
        const val CMD_MENU_POSITION   = "menu-position"
        const val CMD_MENU_TEXT_SIZE  = "menu-text-size"
        const val CMD_ALARM           = "alarm"
        const val CMD_LOGIN           = "login"
        const val CMD_BROWSER         = "browser"

        const val ITN_FREQUENTLY_INFO = "frequently-info"
    }

    override val dialogEvent = SingleLiveEvent<DialogParam>()

    val backgroundAlpha = ObservableFloat(1f)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // COMMAND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            ITN_FREQUENTLY_INFO -> alert(app, R.string.shortcut_link_history)
            else -> super.command(cmd, data)
        }
    }
}