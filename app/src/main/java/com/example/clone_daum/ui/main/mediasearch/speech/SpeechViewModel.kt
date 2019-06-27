package com.example.clone_daum.ui.main.mediasearch.speech

import android.app.Application
import android.text.Spanned
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import brigitte.CommandEventViewModel
import brigitte.ICommandEventAware
import brigitte.arch.SingleLiveEvent
import brigitte.bindingadapter.AnimParams
import brigitte.html
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class SpeechViewModel @Inject constructor(app: Application)
    : CommandEventViewModel(app) {

    val bgScale      = ObservableField<AnimParams>()
    val messageResId = ObservableField(R.string.speech_pls_speak_search_keyword)
    val speechResult = ObservableField("")
    val kakao        = ObservableField("Powered by <b>Kakao</b>".html())
}