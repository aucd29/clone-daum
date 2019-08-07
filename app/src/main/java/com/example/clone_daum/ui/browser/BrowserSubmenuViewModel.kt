package com.example.clone_daum.ui.browser

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.local.BrowserSubMenu
import brigitte.*
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

class BrowserSubmenuViewModel @Inject constructor(application: Application
    , val config: PreloadConfig
) : RecyclerViewModel<BrowserSubMenu>(application) {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserSubmenuViewModel::class.java)

        const val CMD_SUBMENU = "submenu"
    }

    val gridCount = ObservableInt(4)

    init {
        if (mLog.isDebugEnabled) {
            mLog.debug("ITEM (${config.brsSubMenuList.size})\n${config.brsSubMenuList}")
        }

        initAdapter(R.layout.browser_submenu_item)
        items.set(config.brsSubMenuList)
    }
}