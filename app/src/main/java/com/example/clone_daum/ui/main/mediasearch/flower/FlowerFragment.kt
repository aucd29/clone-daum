package com.example.clone_daum.ui.main.mediasearch.flower

import com.example.clone_daum.databinding.FlowerFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import com.example.common.finish
import com.example.common.urlencode
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 31. <p/>
 */

class FlowerFragment: BaseDaggerFragment<FlowerFragmentBinding, FlowerViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(FlowerFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController

    private var mFirstLoad = true

    override fun initViewBinding() {
        mBinding.tensorflow.mResultCallback = {
            it?.let {
                if (mLog.isDebugEnabled) {
                    if (it.size > 0) {
                        mLog.debug("TENSORFLOW : ${it.size}")

                        it.forEach {
                            mLog.debug(it.toString())
                        }

                        mLog.debug("====")
                    }
                }

                if (it.size > 0) {
                    it.get(0).title?.let {
                        mViewModel.message.set(it)
                    }
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

        if (mFirstLoad) {
            mFirstLoad = false
        } else {
            mBinding.tensorflow.resume()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            FlowerViewModel.CMD_BRS_OPEN -> {
                finish()

                mBinding.flowerBack.postDelayed({
                    val url = "https://m.search.daum.net/search?w=tot&q=${data.toString().urlencode()}&DA=13H"
                    viewController.browserFragment(url)
                }, 400)
            }
        }
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