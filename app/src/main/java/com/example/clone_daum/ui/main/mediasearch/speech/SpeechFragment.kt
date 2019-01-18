package com.example.clone_daum.ui.main.mediasearch.speech

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.WindowManager
import com.example.clone_daum.databinding.SpeechFragmentBinding
import com.example.clone_daum.ui.main.mediasearch.MediaSearchViewModel
import com.example.common.BaseDaggerFragment
import com.example.common.OnBackPressedListener
import com.example.common.bindingadapter.AnimParams
import com.example.common.keepScreen
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 18. <p/>
 */

class SpeechFragment: BaseDaggerFragment<SpeechFragmentBinding, MediaSearchViewModel>()
    , OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(SpeechFragment::class.java)
    }

    override fun initViewBinding() = mBinding.run {
        keepScreen(true)
    }

    override fun initViewModelEvents() = mViewModel.run {
        var anim: ObjectAnimator?

        bgScale.set(AnimParams(1.2f, objAniCallback = {
            it.repeatMode  = ValueAnimator.REVERSE
            it.repeatCount = ValueAnimator.INFINITE
            it.duration    = 500
            it.start()

            if (mLog.isDebugEnabled) {
                mLog.debug("START SCALE ANIM")
            }

            anim = it
        }))
    }

    override fun onDestroyView() {
        keepScreen(false)

        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean = true

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