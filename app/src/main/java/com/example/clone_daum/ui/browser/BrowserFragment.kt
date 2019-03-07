package com.example.clone_daum.ui.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserFragmentBinding
import com.example.clone_daum.common.Config
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
import com.google.android.material.snackbar.Snackbar
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserFragment : BaseDaggerFragment<BrowserFragmentBinding, BrowserViewModel>(), OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserFragment::class.java)

        private const val WEBVIEW_SLIDING = 3
    }

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config

    private var mUrl: String? = null
    private var mScrollListener: () -> Unit = {
        if (mBinding.brsWebview.scrollY > config.SCREEN.y) {
            if (mBinding.brsMoveTop.alpha == 0f) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("SHOW BUTTON FOR MOVE TOP")
                }
                mViewModel.brsGoTop.set(AnimParams(1f, duration = 200))
            }
        } else {
            if (mBinding.brsMoveTop.alpha == 1f) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("HIDE BUTTON FOR MOVE TOP")
                }
                mViewModel.brsGoTop.set(AnimParams(0f, duration = 200))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() = mBinding.run {
        mUrl = arguments?.getString("url")

        if (mUrl == null) {
            mLog.error("ERROR: URL : $mUrl")
            return@run
        }

        startAnimation()

        mViewModel.apply {
            brsWebview.defaultSetting(WebViewSettingParams(
                progress = {
                    if (mLog.isTraceEnabled()) {
                        mLog.trace("BRS PROGRESS $it")
                    }

                    valProgress.set(it)
                }, receivedError = {
                    mLog.error("ERROR: $it")

                    it?.let { snackbar(brsWebview, it, Snackbar.LENGTH_LONG)?.show() }
                }, sslError = {
                    mLog.error("ERROR: SSL ")

                    dialog(DialogParam(context = requireContext(),
                        messageId  = R.string.brs_dlg_message_ssl_error,
                        titleId    = R.string.brs_dlg_title_ssl_error,
                        positiveId = R.string.dlg_btn_open,
                        negativeId = R.string.dlg_btn_close,
                        listener   = { res, _ ->
                            if (res) {
                                it?.proceed()
                                sslIconResId.set(R.drawable.ic_vpn_key_red_24dp)
                            } else it?.cancel()
                        }))
                }, pageStarted  = {
                    visibleProgress.set(View.VISIBLE)
                    reloadIconResId.set(R.drawable.ic_clear_black_24dp)
                }, pageFinished = {
                    visibleProgress.set(View.GONE)
                    reloadIconResId.set(R.drawable.ic_replay_black_24dp)
                }
                , canGoForward = { enableForward.set(it) }
                , userAgent    = { config.USER_AGENT }
            ))
        }

        brsWebview.apply {
            loadUrl(mUrl)
            viewTreeObserver.addOnScrollChangedListener(mScrollListener)
        }
    }

    override fun initViewModelEvents() = mViewModel.run {
        if (mUrl == null) {
            return@run
        }

        applyUrl(mUrl!!)

        // 임시 코드 추후 db 에서 얻어오도록 해야함
        applyBrsCount(mBinding.brsArea.childCount)

        sslIconResId.set(R.drawable.ic_vpn_key_black_24dp)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        BrowserViewModel.apply {
            when (cmd) {
                CMD_HOME,
                CMD_BACK             -> onBackPressed()
                CMD_SEARCH_FRAGMENT  -> viewController.searchFragment()
                CMD_SUBMENU_FRAGMENT -> viewController.browserSubFragment { cmd ->
                    when (cmd) {
                        "즐겨찾기목록" -> viewController.favoriteFragment()
                        "즐겨찾기추가" -> {

                        }
                        "방문기록" -> {

                        }
                        "URL 복사" -> mUrl?.let {
                            context?.toast(R.string.brs_copied_url)
                            requireContext().clipboard(it)
                        }
                        "기타 브라우저" -> {
                            confirm(R.string.brs_using_base_brs, R.string.common_notify,
                                listener = { res, dlg -> if (res) showDefaultBrowser() })
                        }
                        "화면 내 검색" -> {
                            // https://code.i-harness.com/en/q/b5bb99
                        }
                        "화면 캡쳐" -> {

                        }
                        "글자 크기" -> {

                        }
                        "홈 화면에 추가" -> {

                        }
                        "전체화면 보기" -> internalFullscreen(true)
                        "앱설정" -> {

                        }
                    }
                }
                CMD_SHARE_EVENT      -> shareLink(data.toString())
                CMD_GOTO_TOP         -> mBinding.brsWebview.scrollTo(0, 0)
                CMD_NORMALSCREEN     -> internalFullscreen(false)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (mLog.isDebugEnabled) {
            mLog.debug("BACK PRESSED")
        }

        mBinding.apply {
            if (brsWebview.canGoBack()) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("BRS BACK")
                }

                brsWebview.goBack()
            } else {
                endAnimation()
                finish()
            }
        }

        return true
    }

    override fun onPause() {
        super.onPause()

        mBinding.brsWebview.pause()
    }

    override fun onResume() {
        mBinding.brsWebview.resume()

        activity?.let { internalFullscreen(it.isFullscreen()) }

        super.onResume()
    }

    override fun onDestroyView() {
        mBinding.brsWebview.apply {
            free()
            viewTreeObserver.removeOnScrollChangedListener(mScrollListener)
        }

        super.onDestroyView()
    }

    private fun startAnimation() = mViewModel.run {
        brsUrlBarAni.set(AnimParams(0f, config.ACTION_BAR_HEIGHT * -1))
        brsAreaAni.set(AnimParams(0f, config.ACTION_BAR_HEIGHT * WEBVIEW_SLIDING))
    }

    private fun endAnimation() = mViewModel.run {
        brsUrlBarAni.set(AnimParams(config.ACTION_BAR_HEIGHT * -1))
        brsAreaAni.set(AnimParams(config.ACTION_BAR_HEIGHT * WEBVIEW_SLIDING))
    }

    private fun shareLink(url: String) {
        val message = string(R.string.brs_intent_app_for_sharing)

        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            setType("text/plain")
            putExtra(Intent.EXTRA_SUBJECT, message)
            putExtra(Intent.EXTRA_TEXT, url)
        }, message))
    }

    private fun showDefaultBrowser() {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setData(Uri.parse(mUrl))
        })
    }

    private fun internalFullscreen(fullscreen: Boolean) {
        activity?.apply {
            fullscreen(fullscreen)
            mViewModel.isFullscreen.set(fullscreen)
        }

        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet
        mBinding.apply {
            ConstraintSet().apply {
                clone(brsContainer)
                connect(brsMoveTop.id, ConstraintSet.BOTTOM, if (fullscreen) {
                    brsNormalScreen.id
                } else {
                    brsBottomLayout.id
                }, ConstraintSet.TOP, 20.dpToPx(requireContext()))
                applyTo(brsContainer)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserFragment
    }
}
