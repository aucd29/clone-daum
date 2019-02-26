package com.example.clone_daum.ui.browser

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

class BrowserSubmenuViewModel @Inject constructor(application: Application
    , config: PreloadConfig
) : RecyclerViewModel<BrowserSubMenu>(application), IFinishFragmentAware
    , ICommandEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserSubmenuViewModel::class.java)

        const val CMD_SUBMENU = "submenu"
    }

    override val finishEvent  = SingleLiveEvent<Void>()
    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()

    val gridCount = ObservableInt(4)

    init {
        if (mLog.isDebugEnabled) {
            mLog.debug("ITEM (${config.brsSubMenuList.size})\n${config.brsSubMenuList}")
        }

        initAdapter("browser_submenu_item")
        items.set(config.brsSubMenuList)
    }
}