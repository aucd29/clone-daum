package com.example.clone_daum.ui.main.navigation

import android.app.Application
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.ui.ViewController
import com.example.common.IFinishFragmentAware
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 20. <p/>
 */

class NavigationViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application), IFinishFragmentAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationViewModel::class.java)

        const val URL_NOTIFICATION = "https://m.daum.net/channel/notice_an"
    }

    override val finishEvent = SingleLiveEvent<Void>()

    val itemSelected = ObservableField<(MenuItem) -> Unit>()
    val newIcon      = ObservableInt(View.GONE)
    val brsOpenEvent = SingleLiveEvent<String>()

    fun eventSetting() {

    }

    fun eventMenuPosition() {

    }

    fun eventWebViewTextSize() {

    }

    fun eventFinishFragment() {
        finishEvent.call()
    }

    fun eventBookMark() {

    }

    fun eventReceivedNoti() {

    }

    fun eventNotification() {
        if (mLog.isDebugEnabled) {
            mLog.debug("BRS OPEN NOTIFICATION")
        }

        brsOpenEvent.value = URL_NOTIFICATION
    }

    fun eventLogin() {

    }
}