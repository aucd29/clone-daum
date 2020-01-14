package com.example.clone_daum.ui.main.alarm

import android.app.Application
import brigitte.RecyclerViewModel
import com.example.clone_daum.model.local.AlarmHistory
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class AlarmViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<AlarmHistory>(app) {

    companion object {
        private val logger = LoggerFactory.getLogger(AlarmViewModel::class.java)
    }
}