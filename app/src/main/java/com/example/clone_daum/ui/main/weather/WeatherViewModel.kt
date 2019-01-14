package com.example.clone_daum.ui.main.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.common.ICommandEventAware
import com.example.common.arch.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 14. <p/>
 */

class WeatherViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), ICommandEventAware {

    companion object {
        const val CMD_MORE_DETAIL = "more-detail"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()
}