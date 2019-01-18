package com.example.clone_daum.ui.main.mediasearch

import android.Manifest
import android.animation.Animator
import android.os.Build
import android.view.animation.*
import androidx.lifecycle.ViewModelProviders
import com.example.clone_daum.databinding.MediaSearchFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
import com.example.common.runtimepermission.PermissionParams
import com.example.common.runtimepermission.runtimePermissions
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 16. <p/>
 *
 */

// 다이얼로그로 하는게 나으려나?
class MediaSearchFragment : BaseDaggerFragment<MediaSearchFragmentBinding, MediaSearchViewModel>()
    , OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(MediaSearchFragment::class.java)

        private const val ANIM_DURATION     = 400L
        private const val ANIM_START_DELAY  = 250L

        private const val REQ_RECORD_VOICE  = 7811
    }

    init {
        mViewModelScope = SCOPE_FRAGMENT
    }

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() = mBinding.run {
        mediaSearchExtendMenuContainer.run {
            layoutListener {
                translationY = height.toFloat() * -1
                mBinding.mediaSearchButtonLayout.translationY =
                        mBinding.mediaSearchButtonLayout.height.toFloat() * -1

                animateIn()
            }
        }

        mediaSearchBackground.run {
            layoutListener {
                // 숨김 영역을 조금 감춤

                val newHeight = height + 20.dpToPx(context!!)
                if (mLog.isDebugEnabled) {
                    mLog.debug("MEDIA SEARCH HEIGHT : ${height} -> ${newHeight}")
                }

                layoutHeight(newHeight)
            }
        }
    }

    override fun initViewModelEvents() { }

    override fun onBackPressed(): Boolean {
        animateOut()
        return true
    }

    private var pauseAnimator: Animator? = null

    private fun animateIn() {
        val mediaSearchButtonLayoutHeight = mBinding.mediaSearchButtonLayout.height.toFloat() * -1
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

        mViewModel.run {
            // animate set 을 쓰는게 나으려나?
            dimmingBgAlpha.set(AnimParams(1f, duration = ANIM_DURATION))
            containerTransY.set(AnimParams(0f, duration = ANIM_DURATION))
            overshootTransY.set(overshootAnim)
        }
    }

    private fun animateOut(endCallback: (() -> Unit)? = null) {
        val searchExtendMenuHeight = mBinding.mediaSearchExtendMenuContainer.height.toFloat() * -1
        val dimmingBgAlphaAnim  = AnimParams(0f, duration = ANIM_DURATION)
        val containerTransYAnim = AnimParams(searchExtendMenuHeight, duration = ANIM_DURATION
            , endListener = {
                finish()
                endCallback?.invoke()
            })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dimmingBgAlphaAnim.startDelay  = ANIM_START_DELAY
            containerTransYAnim.startDelay = ANIM_START_DELAY
            pauseAnimator?.resume()
        }

        mViewModel.run {
            dimmingBgAlpha.set(dimmingBgAlphaAnim)
            containerTransY.set(containerTransYAnim)
        }
    }

    override fun onCommandEvent(cmd: String, data: Any?) = MediaSearchViewModel.run {
        when (cmd) {
            CMD_ANIM_FINISH  -> onBackPressed()
            CMD_SEARCH_VOICE -> runtimePermissions(PermissionParams(activity()
                , arrayListOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                , { req, res ->
                    if (res) {
                        animateOut { viewController.speechFragment() }
                    }
                } , REQ_RECORD_VOICE))
        }

        Unit
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