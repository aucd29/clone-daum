package com.example.clone_daum.ui.main.mediasearch.speech

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.SpeechFragmentBinding
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.ui.Navigator
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager
import com.kakao.sdk.newtoneapi.impl.util.DeviceUtils
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 18. <p/>
 *
 * reference
 *  - https://developers.kakao.com/docs/android/speech
 *  - https://developers.kakao.com/docs/android/getting-started#%ED%82%A4%ED%95%B4%EC%8B%9C-%EB%93%B1%EB%A1%9D
 */
class SpeechFragment @Inject constructor(
) : BaseDaggerFragment<SpeechFragmentBinding, SpeechViewModel>(), OnBackPressedListener,
    SpeechRecognizeListener {
    override val layoutId = R.layout.speech_fragment
    companion object {
        private val logger = LoggerFactory.getLogger(SpeechFragment::class.java)

        private const val V_SCALE          = 1.2F
        private const val V_SCALE_DURATION = 500L
    }

    @Inject lateinit var navigator: Navigator

    // https://code.i-harness.com/ko-kr/q/254ae5
    private val animList = Collections.synchronizedCollection(arrayListOf<ObjectAnimator>())
    private var recognizer: SpeechRecognizerClient? = null

    override fun initViewBinding() = binding.run {
        keepScreen(true)

        if (!DeviceUtils.isSupportedDevice()) {
            alert(R.string.com_kakao_sdk_asr_voice_search_warn_not_support_device
                , listener = { _, _ -> finish() })
            return
        }

        if (context?.isNetworkConntected() != true) {
            alert(R.string.error_network, listener = { _, _ -> finish() })
            return
        }

        initSpeechClient()
    }

    override fun initViewModelEvents() {
    }

    override fun onDestroyView() {
        keepScreen(false)
        endAnimation()
        stopRecording()
        resetSpeechClient()

        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        startRecording()
    }

    override fun onPause() {
        stopRecording()

        super.onPause()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed(): Boolean {
        finish()

        return true
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // UI
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun startAnimation()  = viewModel.run {
        bgScale.set(AnimParams(V_SCALE, objAniCallback = {
            it.apply {
                repeatMode  = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                duration    = V_SCALE_DURATION
                start()

                if (logger.isDebugEnabled) {
                    logger.debug("START SCALE ANIM")
                }

                animList.add(this)
            }
        }))
    }

    private fun endAnimation() {
        animList.forEach { it.cancel() }
        animList.clear()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SPEECH
    //
    ////////////////////////////////////////////////////////////////////////////////////

//        음성 인식 기능을 이용하려면 먼저 SDK 초기화를 해줘야 합니다. 음성 인식 기능을 이용하는 Activity의 onCreate()
//        서 라이브러리를 초기화하는 함수인 SpeechRecognizerManager의 initializeLibrary() 를 호출합니다.
//        그 후 SpeechRecognizerClient.Builder를 통하여 다음과 같이 SpeechRecognizerClient를 생성합니다.
    // https://developers.kakao.com/docs/android/speech#시작하기

    private fun initSpeechClient() {
        // SpeechRecognizerManager 의 appContext 에서 memory leak 존재 ..
        if (!SpeechRecognizerManager.getInstance().isInitialized) {
            SpeechRecognizerManager.getInstance().initializeLibrary(context)
        }

        val builder = SpeechRecognizerClient.Builder()
            .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)
            .setUserDictionary(null)

        recognizer = builder.build()
        recognizer?.setSpeechRecognizeListener(this)
    }

    private fun resetSpeechClient() {
        if (SpeechRecognizerManager.getInstance().isInitialized) {
            SpeechRecognizerManager.getInstance().finalizeLibrary()
        }

        recognizer?.setSpeechRecognizeListener(null)
    }

    private fun startRecording() {
        context?.apply {
            if (!isNetworkConntected()) {
                logger.error("ERROR: NETWORK DISCONNECTED")
                return
            }

            recognizer?.startRecording(true)
        }
    }

    private fun stopRecording() {
        recognizer?.cancelRecording()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SpeechRecognizeListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    // https://developers.kakao.com/docs/android/speech#결과값-및-오류-처리

    // 모든 callback 함수는 UI thread가 아닌 background thread에서 호출될 수 있기 때문에,
    // UI와 관련된 작업은 Activity.runOnUiThread(Runnable) 이나 Handler.post(Runnable)
    // 을 통해 비동기로 호출해야 합니다.
    // 우선 startRecording()이 실행된 이후에 음성 입력을 감지하면 감지된 음성에 대한 결과를 경우에
    // 따라 두가지 callback을 통해 얻을 수 있습니다.

    override fun onReady() {
        if (logger.isDebugEnabled) {
            logger.debug("SPEECH READY")
        }

        startAnimation()
        viewModel.messageResId.set(R.string.speech_listening)
    }

    override fun onFinished() {
        if (logger.isDebugEnabled) {
            logger.debug("SPEECH FINISHED")
        }
//        mViewModel.messageResId.set(R.string.speech_processing)
    }

    override fun onBeginningOfSpeech() {
        if (logger.isDebugEnabled) {
            logger.debug("SPEECH BEGINNING")
        }
    }

    override fun onEndOfSpeech() {
        if (logger.isDebugEnabled) {
            logger.debug("SPEECH END")
        }

        disposable().add(Single.just(null)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                endAnimation()
                viewModel.speechResult.set("")
            }, { errorLog(it, logger) }))
    }

    override fun onAudioLevel(audioLevel: Float) {
        if (audioLevel < 0.2f) {
            return
        }

        if (logger.isTraceEnabled) {
            logger.trace("AUDIO LEVEL : $audioLevel")
        }
    }

//    onPartialResult는 완전히 음성이 종료되기 이전에 현재까지 인식된 음성데이터 문자열을 알려줍니다.
//    이 데이터는 서버에 질의해 데이터를 보정하는 과정을 거치지 않으므로, 다소 부정확할 수 있습니다.
//    중간 인식 결과에 대한 결과가 발생할 때마다 호출되므로 여러번 호출될 수 있습니다.

    override fun onPartialResult(partialResult: String?) {
        if (logger.isDebugEnabled) {
            logger.debug("SPEECH PARTIAL RESULT : $partialResult")
        }

        disposable().add(Single.just(partialResult)
            .observeOn(AndroidSchedulers.mainThread())
            .filter { it.isNotEmpty() }
            .subscribe({
                viewModel.speechResult.set(it)
            }, { errorLog(it, logger) }))
    }

//    onResults는 음성 입력이 종료된 것으로 판단하거나 stopRecording()을 호출한 후에 서버에 질의하는
//    과정까지 마치고 나면 호출됩니다.
//    인식된 문자열은 신뢰도가 높은 값부터 순서대로 Bundle의 SpeechRecognizerClient.
//    KEY_RECOGNITION_RESULTS 값을 통해 ArrayList로 얻을 수 있습니다.
//    신뢰도는 Bundle의 SpeechRecognizerClient.KEY_CONFIDENCE_VALUES 값을 통해
//    ArrayList로 얻을 수 있으며 높은 값부터 순서대로 입니다.
//    신뢰도값은 항상 0보다 크거나 같은 정수이며, 문자열 목록과 같은 개수입니다.

    override fun onResults(results: Bundle?) {
        if (logger.isDebugEnabled) {
            logger.debug("RESULT = $results")
        }

        if (logger.isDebugEnabled) {
            logger.debug("CANCEL RECORDING")
        }

        disposable().add(Single.just(recognizer)
            .subscribeOn(Schedulers.io())
            .map {
                if (logger.isDebugEnabled) {
                    logger.debug("OPEN BRS")
                }

                it.cancelRecording()

                results?.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS) ?: null
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                data?.let {
                    finish()
                    navigator.browserFragment("https://m.search.daum.net/search?w=tot&q=${data[0]}&DA=13H")
                } ?: let {
                    viewModel.messageResId.set(R.string.speech_no_result)
                    finish()

                    binding.speechContainer.postDelayed({
                        finish()
                    }, 100)
                }
            }, { errorLog(it, logger) }))
//        ioThread { mRecognizer?.cancelRecording() }
    }

//    결과를 얻기 위한 callback 말고도 다양한 callback 메서드가 존재합니다.
//    onError callback은 이름에서 알 수 있듯이 에러가 발생했을 때 호출됩니다.
//    SpeechRecognizerClient 에서 다양한 에러 코드에 대응하는 ERROR_ 로 시작하는 code 값들을 확인할 수 있습니다.

    override fun onError(errorCode: Int, errorMsg: String?) {
        logger.error("ERROR: $errorCode $errorMsg")

        disposable().add(Single.just(null)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                endAnimation()
            }, {
                errorLog(it, logger)
            }))

//        uiThread(::endAnimation)

        viewModel.messageResId.apply {
            val error = when (errorCode) {
                SpeechRecognizerClient.ERROR_NO_RESULT -> {
                    set(R.string.speech_no_result)
                    "ERROR_NO_RESULT"
                }
                SpeechRecognizerClient.ERROR_AUDIO_FAIL -> {
                    set(R.string.speech_error_audio)
                    "ERROR_AUDIO_FAIL"
                }
                SpeechRecognizerClient.ERROR_CLIENT -> {
                    set(R.string.speech_error_client)
                    "ERROR_CLIENT"
                }
                SpeechRecognizerClient.ERROR_NETWORK_FAIL -> {
                    set(R.string.speech_error_network)
                    "ERROR_NETWORK_FAIL"
                }
                SpeechRecognizerClient.ERROR_NETWORK_TIMEOUT -> {
                    set(R.string.speech_error_network_timeout)
                    "ERROR_NETWORK_TIMEOUT"
                }
                SpeechRecognizerClient.ERROR_RECOGNITION_TIMEOUT -> {
                    set(R.string.speech_error_recognition)
                    "ERROR_RECOGNITION_TIMEOUT"
                }
                SpeechRecognizerClient.ERROR_SERVER_ALLOWED_REQUESTS_EXCESS -> {
                    set(R.string.speech_error_server)
                    "ERROR_SERVER_ALLOWED_REQUESTS_EXCESS"
                }
                SpeechRecognizerClient.ERROR_SERVER_FAIL -> {
                    set(R.string.speech_error_server)
                    "ERROR_SERVER_FAIL"
                }
                SpeechRecognizerClient.ERROR_SERVER_TIMEOUT -> {
                    set(R.string.speech_error_server)
                    "ERROR_SERVER_TIMEOUT"
                }
                SpeechRecognizerClient.ERROR_SERVER_UNSUPPORT_SERVICE -> {
                    set(R.string.speech_error_server)
                    "ERROR_SERVER_UNSUPPORT_SERVICE"
                }
                else -> {
                    set(R.string.speech_error_unknown)
                    "UNKNOWN"
                }
            }

            logger.error("ERROR: $error")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [SpeechFragmentModule::class])
        abstract fun contributeSpeechFragmentInjector(): SpeechFragment
    }

    @dagger.Module
    abstract class SpeechFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: SpeechFragment): SavedStateRegistryOwner
    }
}