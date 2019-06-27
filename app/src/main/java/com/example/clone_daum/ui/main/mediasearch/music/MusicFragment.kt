package com.example.clone_daum.ui.main.mediasearch.music

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MusicFragmentBinding
import com.example.clone_daum.ui.ViewController
import brigitte.*
import brigitte.bindingadapter.AnimParams
import com.kakao.sdk.newtoneapi.impl.util.DeviceUtils
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class MusicFragment: BaseDaggerFragment<MusicFragmentBinding, MusicViewModel>()
    , OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MusicFragment::class.java)

        private const val V_SCALE          = 1.2F
        private const val V_SCALE_DURATION = 500L
    }

    @Inject lateinit var viewController: ViewController

    // https://code.i-harness.com/ko-kr/q/254ae5
    private val mAnimList = Collections.synchronizedCollection(arrayListOf<ObjectAnimator>())

    override fun initViewBinding() {
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

        initClient()
        startAnimation()

        val current           = 100f
        val animationDuration = 10000 // limit time
        mBinding.musicProgress.setProgressWithAnimation(current, animationDuration)
        mBinding.musicProgress.setOnProgressChangedListener {
            if (it == 100f) {
                dialog(DialogParam("대충 찾았다고 하고", context = context, listener = { _, _ ->
                    finish()
                    viewController.browserFragment("http://www.melon.com/song/detail.htm?songId=30985406&ref=W10600")
                }))
            }
        }
    }

    override fun initViewModelEvents() {
    }

    override fun onDestroyView() {
        keepScreen(false)
        endAnimation()
        resetClient()

        super.onDestroyView()
    }

    private fun startAnimation()  = mViewModel.run {
        bgScale.set(AnimParams(V_SCALE, objAniCallback = {
            it.apply {
                repeatMode  = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                duration    = V_SCALE_DURATION
                start()

                if (mLog.isDebugEnabled) {
                    mLog.debug("START SCALE ANIM")
                }

                mAnimList.add(this)
            }
        }))
    }

    private fun endAnimation() {
        mAnimList.forEach { it.cancel() }
        mAnimList.clear()
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

    private fun initClient() {

    }

    private fun resetClient() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MusicFragment
    }
}