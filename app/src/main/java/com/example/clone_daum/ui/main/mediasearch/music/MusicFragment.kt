package com.example.clone_daum.ui.main.mediasearch.music

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MusicFragmentBinding
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
import com.kakao.sdk.newtoneapi.impl.util.DeviceUtils
import dagger.android.ContributesAndroidInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import org.slf4j.LoggerFactory
import java.util.*


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class MusicFragment: BaseDaggerFragment<MusicFragmentBinding, MusicViewModel>()
    , OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MusicFragment::class.java)

        private val V_SCALE          = 1.2F
        private val V_SCALE_DURATION = 500L
    }

    // https://code.i-harness.com/ko-kr/q/254ae5
    private val mAnimList = Collections.synchronizedCollection(arrayListOf<ObjectAnimator>())

    override fun initViewBinding() {
        keepScreen(true)

        if (!DeviceUtils.isSupportedDevice()) {
            alert(R.string.com_kakao_sdk_asr_voice_search_warn_not_support_device
                , listener = { _, _ -> finish() })
            return
        }

        if (!(context?.isNetworkConntected() ?: false)) {
            alert(R.string.error_network, listener = { _, _ -> finish() })
            return
        }

        initClient()
        animateIn()

        val animationDuration = 500
        var current = 0f

        mDisposable.add(io.reactivex.Observable.interval(500, java.util.concurrent.TimeUnit.MILLISECONDS)
            .take(10)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                current += 10f
                mBinding.musicProgress.setProgressWithAnimation(current, animationDuration)
            })
    }

    override fun initViewModelEvents() {
    }

    override fun onDestroyView() {
        keepScreen(false)
        animateOut()
        resetClient()

        super.onDestroyView()
    }

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
    // UI
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun animateIn()  = mViewModel.run {
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

    private fun animateOut() {
        mAnimList.forEach { it.cancel() }
        mAnimList.clear()
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