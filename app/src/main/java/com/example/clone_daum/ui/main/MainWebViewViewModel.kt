package com.example.clone_daum.ui.main

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.common.IWebViewEventAware
import com.example.common.WebViewEvent
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

class MainWebViewViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application), IWebViewEventAware {

    override val webviewEvent = ObservableField<WebViewEvent>()
}