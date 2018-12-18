package com.example.clone_daum.ui.browser

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.common.bindingadapter.AnimParams
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

class BrowserSubmenuViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application) {

    val submenuLayoutAni = ObservableField<AnimParams>()
}