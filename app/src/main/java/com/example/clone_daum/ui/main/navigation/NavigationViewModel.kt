package com.example.clone_daum.ui.main.navigation

import android.app.Application
import android.view.MenuItem
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import brigitte.arch.SingleLiveEvent
import brigitte.viewmodel.CommandEventViewModel
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 20. <p/>
 */

class NavigationViewModel @Inject constructor(
    application: Application
) : CommandEventViewModel(application) {
    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationViewModel::class.java)

        const val URL_NOTIFICATION = "https://m.daum.net/channel/notice_an"

        const val ITN_SHORTCUT = "shortcut"
        const val ITN_MAIL     = "mail"
        const val ITN_CAFE     = "cafe"

        const val CMD_SETTING           = "setting"
        const val CMD_MENU_POSITION     = "menu-position"
        const val CMD_WEBVIEW_TEXT_SIZE = "webview-text-size"
        const val CMD_RECEIVED_NOTI     = "received-noti"
        const val CMD_BOOKMARK          = "bookmark"
        const val CMD_NOTIFICATION      = "notification"
        const val CMD_LOGIN             = "login"
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

//    val itemSelected    = ObservableField<(MenuItem) -> Unit>()
    val newIcon         = ObservableInt(View.GONE)
    val brsOpenEvent    = SingleLiveEvent<String>()
    val currentItem     = ObservableInt(0)
    val offsetPageLimit = ObservableInt(3)
    val smoothScroll    = ObservableBoolean(false)
    val backgroundAlpha = ObservableFloat(1f)

    fun eventNotification() {
        if (mLog.isDebugEnabled) {
            mLog.debug("BRS OPEN NOTIFICATION")
        }

        brsOpenEvent.value = URL_NOTIFICATION
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // COMMAND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            ITN_SHORTCUT -> currentItem.set(0)
            ITN_MAIL     -> currentItem.set(1)
            ITN_CAFE     -> currentItem.set(2)

            else -> super.command(cmd, data)
        }
    }
}