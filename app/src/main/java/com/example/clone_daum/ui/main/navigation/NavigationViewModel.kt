package com.example.clone_daum.ui.main.navigation

import android.app.Application
import android.view.View
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import brigitte.DialogParam
import brigitte.IDialogAware
import brigitte.arch.SingleLiveEvent
import brigitte.gone
import brigitte.viewmodel.CommandEventViewModel
import brigitte.visible
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

        const val CMD_SETTING        = "setting"

        const val CMD_MAIL          = "mail"
        const val CMD_CAFE          = "cafe"

        const val CMD_ALARM         = "alarm"
        const val CMD_LOGIN         = "login"
        const val CMD_BROWSER       = "browser"
        const val CMD_URL_HISTORY   = "url-history"

        const val CMD_EDIT_HOME_MENU = "edit-home-menu"
        const val CMD_TEXT_SIZE      = "text-size"
        const val CMD_BOOKMARK       = "bookmark"

        const val ITN_FAV_DESC_SHOW = "favorites-desc-show"
        const val ITN_FAV_DESC_HIDE = "favorites-desc-hide"
    }

    override val dialogEvent = SingleLiveEvent<DialogParam>()

    val backgroundAlpha  = ObservableFloat(1f)
    val viewFavoriteDesc = ObservableInt(View.GONE)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // COMMAND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            ITN_FAV_DESC_SHOW -> viewFavoriteDesc.visible()
            ITN_FAV_DESC_HIDE -> viewFavoriteDesc.gone()
            else -> super.command(cmd, data)
        }
    }
}