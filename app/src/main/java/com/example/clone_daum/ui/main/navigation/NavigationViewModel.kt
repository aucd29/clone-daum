package com.example.clone_daum.ui.main.navigation

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
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
    }

    override val finishEvent = SingleLiveEvent<Void>()

    val tabChangedListener = ObservableField<(Int) -> Unit>()


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

    }

    fun eventLogin() {

    }
}