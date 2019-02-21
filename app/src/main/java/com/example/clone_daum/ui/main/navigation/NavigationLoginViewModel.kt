package com.example.clone_daum.ui.main.navigation

import android.app.Application
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class NavigationLoginViewModel @Inject constructor(application: Application)
    : AndroidViewModel(application) {

    val message     = ObservableInt()
    val visibleBtn  = ObservableInt(View.GONE)
}