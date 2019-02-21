package com.example.clone_daum.ui.browser

import android.content.Intent
import android.view.View
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

        brsWebview.loadUrl(mUrl)
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

    override fun onCommandEvent(cmd: String, data: Any) {
        BrowserViewModel.apply {
            when (cmd) {
                CMD_BACK             -> onBackPressed()
                CMD_SEARCH_FRAGMENT  -> viewController.searchFragment()
                CMD_SUBMENU_FRAGMENT -> viewController.browserSubFragment()
                CMD_SHARE_EVENT      -> shareLink(data.toString())
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
                mViewModel.webviewEvent(WebViewEvent.BACK)
            } else {
                endAnimation()
                finish()
            }
        }

        return true
    }

    override fun onPause() {
        super.onPause()

        mViewModel.webviewEvent(WebViewEvent.PAUSE)
    }

    override fun onResume() {
        mViewModel.webviewEvent(WebViewEvent.RESUME)

        super.onResume()
    }

    override fun onDestroyView() {
        mBinding.brsWebview.free()

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
