package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableField
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.common.CommandEventViewModel
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteAddViewModel @Inject constructor(app: Application
) : CommandEventViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteAddViewModel::class.java)
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent  = SingleLiveEvent<Void>()

    val name    = ObservableField<String>()
    val url     = ObservableField<String>()
    val favType = ObservableField<String>()
}