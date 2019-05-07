package com.example.clone_daum.ui.main.mediasearch

import android.Manifest
import android.animation.Animator
import android.os.Build
import android.view.animation.*
import com.example.clone_daum.databinding.MediaSearchFragmentBinding
import com.example.clone_daum.ui.ViewController
import brigitte.*
import brigitte.bindingadapter.AnimParams
import brigitte.runtimepermission.PermissionParams
import brigitte.runtimepermission.runtimePermissions
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 16. <p/>
 */

class MediaSearchFragment : BaseDaggerFragment<MediaSearchFragmentBinding, MediaSearchViewModel>()
    , OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MediaSearchFragment::class.java)

        private const val ANIM_DURATION     = 400L
        private const val ANIM_START_DELAY  = 250L

        private const val REQ_RECORD_SPEECH = 7811
        private const val REQ_RECORD_MUSIC  = 7812
        private const val REQ_FLOWER        = 7813
        private const val REQ_BARCODE       = 7814
    }

    @Inject lateinit var viewController: ViewController

    private var mPauseAnimator: Animator? = null

    override fun initViewBinding() = mBinding.run {
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
                if (mLog.isDebugEnabled) {
                    mLog.debug("MEDIA SEARCH HEIGHT : ${height} -> ${newHeight}")
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
        val mediaSearchButtonLayoutHeight = mBinding.mediaSearchButtonLayout.height.toFloat() * -1
        val overshootAnim = AnimParams(0f
            , initValue    = mediaSearchButtonLayoutHeight
            , duration     = ANIM_DURATION
            , startDelay   = ANIM_START_DELAY
            , interpolator = OvershootInterpolator(2.3f)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            overshootAnim.reverse = {
                mPauseAnimator = it
                it?.pause()
            }
        }

        mViewModel.apply {
            // animate set 을 쓰는게 나으려나?
            dimmingBgAlpha.set(AnimParams(1f, duration = ANIM_DURATION))
            containerTransY.set(AnimParams(0f, duration = ANIM_DURATION))
            overshootTransY.set(overshootAnim)
        }
    }

    private fun endAnimation(endCallback: (() -> Unit)? = null) {
        val searchExtendMenuHeight = mBinding.mediaSearchExtendMenuContainer.height.toFloat() * -1
        val dimmingBgAlphaAnim  = AnimParams(0f, duration = ANIM_DURATION)
        val containerTransYAnim = AnimParams(searchExtendMenuHeight, duration = ANIM_DURATION
            , endListener = { v, anim ->
                finish()
                endCallback?.invoke()
            })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dimmingBgAlphaAnim.startDelay  = ANIM_START_DELAY
            containerTransYAnim.startDelay = ANIM_START_DELAY
            mPauseAnimator?.resume()
        }

        mViewModel.apply {
            dimmingBgAlpha.set(dimmingBgAlphaAnim)
            containerTransY.set(containerTransYAnim)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed(): Boolean {
        if (mLog.isDebugEnabled) {
            mLog.debug("BACK PRESSED")
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
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND EVENT : $cmd")
        }

        MediaSearchViewModel.apply {
            when (cmd) {
                CMD_ANIM_FINISH    -> onBackPressed()
                CMD_SEARCH_SPEECH  -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        , { req, res -> if (res) { viewController.speechFragment() } }
                        , REQ_RECORD_SPEECH))
                }

                CMD_SEARCH_MUSIC   -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        , { req, res -> if (res) { viewController.musicFragment() } }
                        , REQ_RECORD_MUSIC))
                }

                CMD_SEARCH_FLOWER  -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.CAMERA)
                        , { req, res -> if (res) { viewController.flowerFragment() } }
                        , REQ_FLOWER))
                }

                CMD_SEARCH_BARCODE -> endAnimation {
                    runtimePermissions(PermissionParams(activity()
                        , arrayListOf(Manifest.permission.CAMERA)
                        , { req, res -> if (res) { viewController.barcodeFragment() } }
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
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MediaSearchFragment
    }
}
