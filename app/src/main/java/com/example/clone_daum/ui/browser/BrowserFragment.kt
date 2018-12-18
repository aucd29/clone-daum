package com.example.clone_daum.ui.browser

import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import com.example.common.bindingadapter.AnimParams
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.GridHolder
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserFragment : BaseDaggerFragment<BrowserFragmentBinding, BrowserViewModel>(), OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserFragment::class.java)

        const val WEBVIEW_SLIDING = 3
    }

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config

    override fun settingEvents() = mViewModel.run {
        val url = arguments?.getString("url")
        if (url == null) {
            mLog.error("ERROR: URL : $url")
            return
        }

        sslIconResId.set(R.drawable.ic_vpn_key_black_24dp)

        animateIn()
        applyUrl(url)

        mBinding.run {
            brsWebview.loadUrl(url)
            applyBrsCount(brsArea.childCount)  // 임시 코드 추후 db 에서 얻어오도록 해야함
        }

        observe(backEvent)    { onBackPressed() }
        observe(searchEvent)  { viewController.searchFragment() }
        observe(shareEvent)   { shareLink(it) }
        observe(submenuEvent) { viewController.browserSubFragment(R.id.brs_container, childFragmentManager) }

        brsSetting.set(WebViewSettingParams(
            progress = {
                if (mLog.isTraceEnabled()) {
                    mLog.trace("BRS PROGRESS $it")
                }

                valProgress.set(it)
            }, receivedError = {
                mLog.error("ERROR: $it")

                it?.let { snackbar(mBinding.brsWebview, it, Snackbar.LENGTH_LONG)?.show() }
            }, sslError = {
                mLog.error("ERROR: SSL ")

                dialog(DialogParam(string(R.string.brs_dlg_message_ssl_error),
                    title        = string(R.string.brs_dlg_title_ssl_error),
                    positiveStr  = string(R.string.dlg_btn_open),
                    negativeStr  = string(R.string.dlg_btn_close),
                    listener     = { res, _ ->
                        if (res) {
                            it?.proceed()
                            sslIconResId.set(R.drawable.ic_vpn_key_red_24dp)
                        } else it?.cancel()
                    }))
            } , pageStarted  = {
                visibleProgress.set(View.VISIBLE)
                reloadIconResId.set(R.drawable.ic_clear_black_24dp)
            } , pageFinished = {
                visibleProgress.set(View.GONE)
                reloadIconResId.set(R.drawable.ic_replay_black_24dp)
            }
            , canGoForward = { enableForward.set(it) }
            , userAgent    = { config.USER_AGENT }
        ))
    }

    override fun onBackPressed() = mBinding.run {
        if (brsWebview.canGoBack()) {
            mViewModel.brsEvent.set(WebViewEventParams(event = WebViewEvent.BACK))
        } else {
            animateOut()
            finish()
        }

        // 이곳에서 back 관련 처리를 함
        true
    }

    override fun onPause() {
        super.onPause()

        mViewModel.brsEvent.set(WebViewEventParams(event = WebViewEvent.PAUSE_TIMER))
    }

    override fun onResume() {
        mViewModel.brsEvent.set(WebViewEventParams(event = WebViewEvent.RESUME_TIMER))

        super.onResume()
    }

    private fun animateIn() = mViewModel.run {
        brsUrlBarAni.set(AnimParams(0f, config.ACTION_BAR_HEIGHT * -1))
        brsAreaAni.set(AnimParams(0f, config.ACTION_BAR_HEIGHT * WEBVIEW_SLIDING))
    }

    private fun animateOut() = mViewModel.run {
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
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserFragment
    }
}
