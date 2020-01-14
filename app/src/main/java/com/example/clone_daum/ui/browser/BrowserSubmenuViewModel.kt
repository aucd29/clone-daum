package com.example.clone_daum.ui.browser

import android.app.Application
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.local.BrowserSubMenu
import brigitte.*
import brigitte.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

class BrowserSubmenuViewModel @Inject constructor(
    val config: PreloadConfig,
    app: Application
) : RecyclerViewModel2<BrowserSubMenu>(app) {
    companion object {
        private val logger = LoggerFactory.getLogger(BrowserSubmenuViewModel::class.java)

        const val CMD_SUBMENU = "submenu"
    }

    val gridCount = ObservableInt(4)
    val dismiss   = SingleLiveEvent<Void>()

    init {
        if (logger.isDebugEnabled) {
            logger.debug("ITEM (${config.brsSubMenuList.size})\n${config.brsSubMenuList}")
        }

        items.set(config.brsSubMenuList)
    }
}