package com.example.clone_daum.ui.main.mediasearch.speech

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.clone_daum.R
import com.example.clone_daum.databinding.SpeechFragmentBinding
import com.example.clone_daum.ui.main.mediasearch.MediaSearchViewModel
import com.example.common.BaseDaggerFragment
import com.example.common.OnBackPressedListener
import com.example.common.bindingadapter.AnimParams
import com.example.common.finish
import com.example.common.keepScreen
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager
import com.kakao.sdk.newtoneapi.impl.util.DeviceUtils
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 18. <p/>
 */

/*
SpeechSampleActivity.java

// 웹 검색
SpeechRecognizerClient.SERVICE_TYPE_WEB

Intent i = new Intent(getApplicationContext(), VoiceRecoActivity.class);

if (serviceType.equals(SpeechRecognizerClient.SERVICE_TYPE_WORD)) {
        EditText words = (EditText)findViewById(R.id.words_edit);
        String wordList = words.getText().toString();

    Log.i("SpeechSampleActivity", "word list : " + wordList.replace('\n', ','));

        i.putExtra(SpeechRecognizerActivity.EXTRA_KEY_USER_DICTIONARY, wordList);
}

i.putExtra(SpeechRecognizerActivity.EXTRA_KEY_SERVICE_TYPE, serviceType);

startActivityForResult(i, 0);


 */

class SpeechFragment: BaseDaggerFragment<SpeechFragmentBinding, MediaSearchViewModel>()
    , OnBackPressedListener, SpeechRecognizeListener {

    companion object {
        private val mLog = LoggerFactory.getLogger(SpeechFragment::class.java)

        private val V_SCALE          = 1.2F
        private val V_SCALE_DURATION = 500L
    }

    lateinit var recognizer: SpeechRecognizerClient

    // https://code.i-harness.com/ko-kr/q/254ae5
    val animList = Collections.synchronizedCollection(arrayListOf<ObjectAnimator>())

    override fun initViewBinding() = mBinding.run {
        keepScreen(true)

        if (!DeviceUtils.isSupportedDevice()) {
            mViewModel.speechMessageResId.set(R.string.com_kakao_sdk_asr_voice_search_warn_not_support_device)
            return
        }

        initSpeechRecognizerClient()
    }

    override fun initViewModelEvents() = mViewModel.run {

    }

    override fun onDestroyView() {
        keepScreen(false)

        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        finish()

        return true
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // UI
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun animateIn()  = mViewModel.run {
        bgScale.set(AnimParams(V_SCALE, objAniCallback = {
            it.run {
                repeatMode  = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                duration    = V_SCALE_DURATION
                start()

                if (mLog.isDebugEnabled) {
                    mLog.debug("START SCALE ANIM")
                }

                animList.add(this)
            }
        }))
    }

    private fun animateOut() {
        animList.forEach {
            it.cancel()
        }
        animList.clear()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SPEECH
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun initSpeechRecognizerClient() {
        if (!SpeechRecognizerManager.getInstance().isInitialized) {
            SpeechRecognizerManager.getInstance().initializeLibrary(context)
        }

        val builder = SpeechRecognizerClient.Builder()
//            .setUserDictionary(intent.getStringExtra(EXTRA_KEY_USER_DICTIONARY))
            .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)

        recognizer = builder.build()
        recognizer.setSpeechRecognizeListener(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SpeechRecognizeListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onReady() {
    }

    override fun onFinished() {
    }

    override fun onPartialResult(partialResult: String?) {
    }

    override fun onBeginningOfSpeech() = animateIn()

    override fun onEndOfSpeech() = animateOut()

    override fun onAudioLevel(audioLevel: Float) {
    }

    override fun onError(errorCode: Int, errorMsg: String?) {

    }

    override fun onResults(results: Bundle?) {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): SpeechFragment
    }
}