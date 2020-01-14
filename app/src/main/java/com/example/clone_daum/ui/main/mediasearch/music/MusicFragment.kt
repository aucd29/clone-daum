package com.example.clone_daum.ui.main.mediasearch.music

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MusicFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.di.dagger.scope.FragmentScope
import com.kakao.sdk.newtoneapi.impl.util.DeviceUtils
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 29. <p/>
 */

class MusicFragment @Inject constructor(
) : BaseDaggerFragment<MusicFragmentBinding, MusicViewModel>(), OnBackPressedListener {
    override val layoutId = R.layout.music_fragment
    companion object {
        private val logger = LoggerFactory.getLogger(MusicFragment::class.java)

        private const val V_SCALE          = 1.2F
        private const val V_SCALE_DURATION = 500L
    }

    @Inject lateinit var navigator: Navigator

    // https://code.i-harness.com/ko-kr/q/254ae5
    private val animList = Collections.synchronizedCollection(arrayListOf<ObjectAnimator>())

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
        binding.musicProgress.setProgressWithAnimation(current, animationDuration)
        binding.musicProgress.setOnProgressChangedListener {
            if (it == 100f) {
                dialog(DialogParam("대충 찾았다고 하고", context = context, listener = { _, _ ->
                    finish()
                    navigator.browserFragment("http://www.melon.com/song/detail.htm?songId=30985406&ref=W10600")
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
        @FragmentScope
        @ContributesAndroidInjector(modules = [MusicFragmentModule::class])
        abstract fun contributeMusicFragmentInjector(): MusicFragment
    }

    @dagger.Module
    abstract class MusicFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: MusicFragment): SavedStateRegistryOwner
    }
}