package com.example.clone_daum.ui.main.mediasearch

import android.Manifest
import android.animation.Animator
import android.os.Build
import android.view.animation.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.MediaSearchFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.di.dagger.scope.FragmentScope
import brigitte.runtimepermission.PermissionParams
import brigitte.runtimepermission.runtimePermissions
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 16. <p/>
 */

class MediaSearchFragment @Inject constructor(
) : BaseDaggerFragment<MediaSearchFragmentBinding, MediaSearchViewModel>(), OnBackPressedListener {
    override val layoutId = R.layout.media_search_fragment

    companion object {
        private val logger = LoggerFactory.getLogger(MediaSearchFragment::class.java)

        private const val ANIM_DURATION     = 400L
        private const val ANIM_START_DELAY  = 250L

        private const val REQ_RECORD_SPEECH = 7811
        private const val REQ_RECORD_MUSIC  = 7812
        private const val REQ_FLOWER        = 7813
        private const val REQ_BARCODE       = 7814
    }

    @Inject lateinit var navigator: Navigator

    private var pauseAnimator: Animator? = null

    override fun initViewBinding() = binding.run {
        mediaSearchExtendMenuContainer.apply {
            globalLayoutListener {
                translationY = height.toFloat() * -1
                mediaSearchButtonLayout.translationY = mediaSearchButtonLayout.height.toFloat() * -1

                startAnimation()

                true
            }
        }

        mediaSearchBackground.apply {
            globalLayoutListener {
                // 숨김 영역을 조금 감춤

                val newHeight = height + 20.dpToPx(requireContext())
                if (logger.isDebugEnabled) {
                    logger.debug("MEDIA SEARCH HEIGHT : $height -> $newHeight")
                }

                layoutHeight(newHeight)

                true
            }
        }

        Unit
    }

    override fun initViewModelEvents() {
    }

    private fun startAnimation() {
        val mediaSearchButtonLayoutHeight = binding.mediaSearchButtonLayout.height.toFloat() * -1
        val overshootAnim = AnimParams(0f
            , initValue    = mediaSearchButtonLayoutHeight
            , duration     = ANIM_DURATION
            , startDelay   = ANIM_START_DELAY
            , interpolator = OvershootInterpolator(2.3f)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            overshootAnim.reverse = {
                pauseAnimator = it
                it?.pause()
            }
        }

        viewModel.apply {
            // animate set 을 쓰는게 나으려나?
            dimmingBgAlpha.set(AnimParams(1f, duration = ANIM_DURATION))
            containerTransY.set(AnimParams(0f, duration = ANIM_DURATION))
            overshootTransY.set(overshootAnim)
        }
    }

    private fun endAnimation(endCallback: (() -> Unit)? = null) {
        val searchExtendMenuHeight = binding.mediaSearchExtendMenuContainer.height.toFloat() * -1
        val dimmingBgAlphaAnim  = AnimParams(0f, duration = ANIM_DURATION)
        val containerTransYAnim = AnimParams(searchExtendMenuHeight, duration = ANIM_DURATION
            , endListener = { anim ->
                finish()
                endCallback?.invoke()
            })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dimmingBgAlphaAnim.startDelay  = ANIM_START_DELAY
            containerTransYAnim.startDelay = ANIM_START_DELAY
            pauseAnimator?.resume()
        }

        viewModel.apply {
            dimmingBgAlpha.set(dimmingBgAlphaAnim)
            containerTransY.set(containerTransYAnim)
        }
    }

    override fun onDestroyView() {
        viewModel.apply {
            dimmingBgAlpha.set(null)
            containerTransY.set(null)
        }

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed(): Boolean {
        if (logger.isDebugEnabled) {
            logger.debug("BACK PRESSED")
        }

        endAnimation()
        return true
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        if (logger.isDebugEnabled) {
            logger.debug("COMMAND EVENT : $cmd")
        }

        MediaSearchViewModel.apply {
            when (cmd) {
                CMD_ANIM_FINISH    -> onBackPressed()
                CMD_SEARCH_SPEECH  -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        , { _, res -> if (res) { navigator.speechFragment() } }
                        , REQ_RECORD_SPEECH))
                }

                CMD_SEARCH_MUSIC   -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        , { _, res -> if (res) { navigator.musicFragment() } }
                        , REQ_RECORD_MUSIC))
                }

                CMD_SEARCH_FLOWER  -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.CAMERA)
                        , { _, res -> if (res) { navigator.flowerFragment() } }
                        , REQ_FLOWER))
                }

                CMD_SEARCH_BARCODE -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.CAMERA)
                        , { _, res -> if (res) { navigator.barcodeFragment() } }
                        , REQ_BARCODE))
                }
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
        @ContributesAndroidInjector(modules = [MediaSearchFragmentModule::class])
        abstract fun contributeMediaSearchFragmentInjector(): MediaSearchFragment
    }

    @dagger.Module
    abstract class MediaSearchFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: MediaSearchFragment): SavedStateRegistryOwner
    }
}
