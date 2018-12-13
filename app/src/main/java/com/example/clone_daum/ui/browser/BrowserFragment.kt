package com.example.clone_daum.ui.browser

import android.os.Bundle
import android.view.View
import com.example.clone_daum.Config
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserFragmentBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import com.google.android.material.snackbar.Snackbar
import dagger.android.ContributesAndroidInjector
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserFragment : BaseRuleFragment<BrowserFragmentBinding>(), OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserFragment::class.java)

        const val ANI_DURATION      = 300L
        const val WEBVIEW_SLIDING   = 3
    }

    @Inject lateinit var disposable: CompositeDisposable
    @Inject lateinit var vmFactory: DaggerViewModelFactory
    @Inject lateinit var viewController: ViewController

    lateinit var viewmodel: BrowserViewModel

    override fun bindViewModel() {
        viewmodel = vmFactory.inject(this, BrowserViewModel::class.java)
        mBinding.model = viewmodel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewmodel.sslIconResId.set(R.drawable.ic_vpn_key_black_24dp)

        val url = arguments?.getString("url")
        url?.let {
            animateIn()

            mBinding.run {
                brsWebview.run {
                    defaultSetting(pageStarted = {
                        viewmodel.visibleProgress.set(View.VISIBLE)
                    }, progress = {
                        if (mLog.isTraceEnabled()) {
                            mLog.trace("BRS PROGRESS $it")
                        }

                        viewmodel.valProgress.set(it)
                    }, pageFinished = {
                        viewmodel.visibleProgress.set(View.GONE)
                    }, receivedError = {
                        mLog.error("ERROR: $it")

                        it?.let { snackbar(this, it, Snackbar.LENGTH_LONG)?.show() }
                    }, sslError = {
                        mLog.error("ERROR: SSL ")

                        dialog(DialogParam(string(R.string.brs_dlg_message_ssl_error)!!,
                            title        = string(R.string.brs_dlg_title_ssl_error),
                            positiveStr  = string(R.string.dlg_btn_open),
                            negativeStr  = string(R.string.dlg_btn_close),
                            listener     = { res, _ ->
                                if (res) {
                                    it?.proceed()
                                    viewmodel.sslIconResId.set(R.drawable.ic_vpn_key_red_24dp)
                                } else it?.cancel()
                            }))
                    }, canGoForward = {
                        viewmodel.enableForward.set(it)
                    })
                    settings.userAgentString = Config.USER_AGENT

                    loadUrl(url)
                }
            }

            settingEvents(it)
        }
    }

    private fun settingEvents(url: String) = viewmodel.run {
        applyUrl(url)
        observe(backEvent) { onBackPressed() }

        mBinding.run {
            applyBrsCount(brsArea.childCount)

            observe(reloadEvent) { brsWebview.reload() }
            observe(forwardEvent) { brsWebview.goForward() }
            observe(homeEvent) { finish() }
            observe(searchEvent) { viewController.searchFragment() }
            observe(submenuEvent) {  }
        }
    }

    private fun animateIn() = mBinding.run {
        brsUrlBar.run {
            val brsUrlBarY = getActionBarHeight()
            translationY = brsUrlBarY * -1
            animate().translationY(0f).setDuration(ANI_DURATION).start()
        }

        brsArea.run {
            val brsAreaY = getActionBarHeight() * WEBVIEW_SLIDING
            translationY = brsAreaY
            animate().translationY(0f).setDuration(ANI_DURATION).start()
        }
    }

    private fun animateOut() = mBinding.run {
        brsUrlBar.animate().translationY(getActionBarHeight() * -1)
                .setDuration(ANI_DURATION).start()

        brsArea.animate().translationY(getActionBarHeight() * WEBVIEW_SLIDING)
                .setDuration(ANI_DURATION).start()
    }

    private fun finish() {
        activity().supportFragmentManager.pop()
    }

    override fun onBackPressed(): Boolean {
        mBinding.run {
            if (brsWebview.canGoBack()) {
                brsWebview.goBack()
            } else {
                animateOut()
                finish()
            }
        }

        // 이곳에서 back 관련 처리를 함
        return true
    }

    override fun onPause() {
        super.onPause()
        mBinding.brsWebview.pauseTimers()
    }

    override fun onResume() {
        mBinding.brsWebview.resumeTimers()
        super.onResume()
    }

    private fun getActionBarHeight(): Float {
        val ta = context!!.theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        return ta.getDimension(0, 0f)
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
