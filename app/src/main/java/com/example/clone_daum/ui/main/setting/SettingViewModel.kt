package com.example.clone_daum.ui.main.setting

import android.app.Application
import brigitte.RecyclerViewModel
import com.example.clone_daum.model.local.Setting
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class SettingViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<Setting>(app) {

    companion object {
        private val mLog = LoggerFactory.getLogger(SettingViewModel::class.java)
    }
}