package com.example.clone_daum.ui.main.login

import android.app.Application
import brigitte.viewmodel.CommandEventViewModel
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class LoginViewModel @Inject constructor(

    app: Application
) : CommandEventViewModel(app) {

    companion object {
        private val mLog = LoggerFactory.getLogger(LoginViewModel::class.java)
    }
}