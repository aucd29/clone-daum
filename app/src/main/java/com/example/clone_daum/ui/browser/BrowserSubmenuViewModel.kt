package com.example.clone_daum.ui.browser

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.common.IFinishFragmentAware
import com.example.common.RecyclerViewModel
import com.example.common.app
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

class BrowserSubmenuViewModel @Inject constructor(application: Application, config: PreloadConfig)
    : RecyclerViewModel<BrowserSubMenu>(application)
    , IFinishFragmentAware {

    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserSubmenuViewModel::class.java)
    }

    val gridCount = ObservableInt(4)

    override val finishEvent: SingleLiveEvent<Void> = SingleLiveEvent()

    init {
        if (mLog.isDebugEnabled) {
            mLog.debug("ITEM (${config.brsSubMenu.size})\n${config.brsSubMenu}")
        }

        initAdapter("browser_submenu_item")
        items.set(config.brsSubMenu)
    }

    fun event(event: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("EVENT : $event")
        }

        finishEvent.call()
    }
}