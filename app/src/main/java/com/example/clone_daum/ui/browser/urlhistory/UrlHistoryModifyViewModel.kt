package com.example.clone_daum.ui.browser.urlhistory

import android.app.Application
import com.example.clone_daum.model.local.UrlHistory
import com.example.common.RecyclerViewModel
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryModifyViewModel @Inject constructor(application: Application
) : RecyclerViewModel<UrlHistory>(application) {

    companion object {
        private val mLog = LoggerFactory.getLogger(UrlHistoryModifyViewModel::class.java)
    }
}