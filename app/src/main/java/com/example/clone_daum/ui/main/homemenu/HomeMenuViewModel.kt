package com.example.clone_daum.ui.main.homemenu

import android.app.Application
import brigitte.RecyclerViewModel
import com.example.clone_daum.model.local.HomeMenu
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class HomeMenuViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<HomeMenu>(app) {

    companion object {
        private val logger = LoggerFactory.getLogger(HomeMenuViewModel::class.java)
    }
}