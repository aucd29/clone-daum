package com.example.clone_daum.ui.main.hometext

import android.app.Application
import brigitte.viewmodel.CommandEventViewModel
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class HomeTextViewModel @Inject constructor(
    app: Application
) : CommandEventViewModel(app) {

    companion object {
        private val mLog = LoggerFactory.getLogger(HomeTextViewModel::class.java)
    }
}