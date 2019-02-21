package com.example.clone_daum.ui.main.mediasearch.flower

import com.example.clone_daum.databinding.FlowerFragmentBinding
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 31. <p/>
 */

class FlowerFragment: BaseDaggerFragment<FlowerFragmentBinding, FlowerViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(FlowerFragment::class.java)
    }

    override fun initViewBinding() {
        mBinding.tensorflow.mResultCallback = {
            it?.let {
                if (mLog.isDebugEnabled) {
                    mLog.debug("TENSORFLOW : ${it.size}")

                    it.forEach {
                        mLog.debug(it.toString())
                    }

                    mLog.debug("====")
                }
            }
        }
    }

    override fun initViewModelEvents() {
    }

    override fun onPause() {
        mBinding.tensorflow.pause()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        mBinding.tensorflow.resume()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FlowerFragment
    }
}