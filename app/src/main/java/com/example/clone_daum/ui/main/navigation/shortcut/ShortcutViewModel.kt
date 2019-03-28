package com.example.clone_daum.ui.main.navigation.shortcut

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import com.example.common.DialogParam
import com.example.common.IDialogAware
import com.example.common.app
import com.example.common.arch.SingleLiveEvent
import com.example.common.string
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class ShortcutViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), IDialogAware {

    override val dialogEvent = SingleLiveEvent<DialogParam>()

    val brsSitemapEvent      = SingleLiveEvent<String>()

    fun eventSitemap() {
        brsSitemapEvent.value = "https://m.daum.net/site.daum"
    }

    fun eventFrequentlySite() {
        alert(app, R.string.shortcut_link_history)
    }
}