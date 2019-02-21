package com.example.clone_daum.ui.main.mediasearch.speech

import android.app.Application
import android.text.Spanned
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.R
import com.example.common.IFinishFragmentAware
import com.example.common.arch.SingleLiveEvent
import com.example.common.bindingadapter.AnimParams
import com.example.common.html
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class SpeechViewModel @Inject constructor(app: Application)
    : AndroidViewModel(app), IFinishFragmentAware {

    override val finishEvent  = SingleLiveEvent<Void>()

    val bgScale      = ObservableField<AnimParams>()
    val messageResId = ObservableField<Int>(R.string.speech_pls_speak_search_keyword)
    val speechResult = ObservableField<String>("")
    val kakao        = ObservableField<Spanned>("Powered by <b>Kakao</b>".html())
}