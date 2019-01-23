package com.example.clone_daum.ui.main.mediasearch.speech

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.clone_daum.R
import com.example.clone_daum.databinding.SpeechFragmentBinding
import com.example.clone_daum.ui.main.mediasearch.MediaSearchViewModel
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager
import com.kakao.sdk.newtoneapi.impl.util.DeviceUtils
import com.kakao.sdk.newtoneapi.impl.view.SpecialSearchRenderer
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


 // https://developers.kakao.com/docs/android/speech

 */

class SpeechFragment: BaseDaggerFragment<SpeechFragmentBinding, MediaSearchViewModel>()
    , OnBackPressedListener, SpeechRecognizeListener {

    companion object {
        private val mLog = LoggerFactory.getLogger(SpeechFragment::class.java)

        private val V_SCALE          = 1.2F
        private val V_SCALE_DURATION = 500L
    }

    var recognizer: SpeechRecognizerClient? = null

    // https://code.i-harness.com/ko-kr/q/254ae5
    val animList = Collections.synchronizedCollection(arrayListOf<ObjectAnimator>())

    override fun initViewBinding() = mBinding.run {
        keepScreen(true)

        if (!DeviceUtils.isSupportedDevice()) {
            mViewModel.speechMessageResId.set(R.string.com_kakao_sdk_asr_voice_search_warn_not_support_device)
            return
        }

        // 인증 오류로 일단 대기
        initSpeechRecognizerClient()
    }

    override fun initViewModelEvents() = mViewModel.run {

    }

    override fun onDestroyView() {
        keepScreen(false)

        if (SpeechRecognizerManager.getInstance().isInitialized) {
            SpeechRecognizerManager.getInstance().finalizeLibrary()
        }

        recognizer?.setSpeechRecognizeListener(null)

        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        finish()

        return true
    }

    override fun onResume() {
        super.onResume()

        startRecording()
    }

    override fun onPause() {
        stopRecording()

        super.onPause()
    }

    private fun startRecording() {
        context?.run {
            if (!isNetworkConntected()) {
                mLog.error("ERROR: NETWORK DISCONNECTED")
                return
            }

            recognizer?.startRecording(true)?.let {
                    if (!it) {
                        mLog.error("ERROR: ALREADY_STARTED")

                        return
                    }

                systemService(AudioManager::class.java)?.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
                    } else {
                        setStreamMute(AudioManager.STREAM_MUSIC, true)
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        recognizer?.cancelRecording()

        context?.systemService(AudioManager::class.java)?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
            } else {
                setStreamMute(AudioManager.STREAM_MUSIC, false)
            }
        }
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
        animList.forEach { it.cancel() }
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
            .setUserDictionary(null)
            .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)

        recognizer = builder.build()
        recognizer?.setSpeechRecognizeListener(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SpeechRecognizeListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onReady() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SPEECH READY")
        }
    }

    override fun onFinished() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SPEECH FINISHED")
        }
    }

    override fun onPartialResult(partialResult: String?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("SPEECH PARTIAL RESULT")
        }
    }

    override fun onBeginningOfSpeech() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SPEECH BEGINNING")
        }

        animateIn()
    }

    override fun onEndOfSpeech() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SPEECH END")
        }

        animateOut()
    }

    override fun onAudioLevel(audioLevel: Float) {
        if (audioLevel < 0.2f) {
            return
        }

        if (mLog.isTraceEnabled) {
            mLog.trace("AUDIO LEVEL : $audioLevel")
        }
    }

    override fun onError(errorCode: Int, errorMsg: String?) {
        mLog.error("ERROR: $errorMsg")

    }

    override fun onResults(results: Bundle?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("RESULT = $results")
        }
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