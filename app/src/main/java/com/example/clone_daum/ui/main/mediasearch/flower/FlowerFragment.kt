package com.example.clone_daum.ui.main.mediasearch.flower

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.FlowerFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import brigitte.finish
import brigitte.urlencode
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 31. <p/>
 */

class FlowerFragment @Inject constructor(
) : BaseDaggerFragment<FlowerFragmentBinding, FlowerViewModel>() {
    override val layoutId = R.layout.flower_fragment

    companion object {
        private val logger = LoggerFactory.getLogger(FlowerFragment::class.java)
    }

    @Inject lateinit var navigator: Navigator

    private var firstLoad = true

    override fun initViewBinding() {
        binding.tensorflow.mResultCallback = { it?.let {
            if (logger.isDebugEnabled) {
                if (it.isNotEmpty()) {
                    logger.debug("TENSORFLOW : ${it.size}")

                    it.forEach { tf ->
                        logger.debug(tf.toString())
                    }

                    logger.debug("====")
                }
            }

            if (it.isNotEmpty()) {
                it[0].title?.let {
                    viewModel.message.set(it)
                }
            }
        } }
    }

    override fun initViewModelEvents() {
    }

    override fun onPause() {
        binding.tensorflow.pause()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (firstLoad) {
            firstLoad = false
        } else {
            binding.tensorflow.resume()
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

                binding.flowerBack.postDelayed({
                    val url = "https://m.search.daum.net/search?w=tot&q=${data.toString().urlencode()}&DA=13H"
                    navigator.browserFragment(url)
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
        @FragmentScope
        @ContributesAndroidInjector(modules = [FlowerFragmentModule::class])
        abstract fun contributeFlowerFragmentInjector(): FlowerFragment
    }

    @dagger.Module
    abstract class FlowerFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: FlowerFragment): SavedStateRegistryOwner
    }
}