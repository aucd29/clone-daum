package com.example.clone_daum.ui.browser

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import com.example.clone_daum.Config
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserFragmentBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
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
    }

    @Inject lateinit var disposable: CompositeDisposable
    @Inject lateinit var vmfactory: DaggerViewModelFactory

    lateinit var viewmodel: BrowserViewModel

    override fun bindViewModel() {
        viewmodel = vmfactory.inject(this, BrowserViewModel::class.java)
        mBinding.model = viewmodel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewmodel.sslIconResId.set(R.drawable.ic_vpn_key_black_24dp)

        val url = arguments?.getString("url")
        url?.let {
            mBinding.run {
                brsWebview.run {
                    defaultSetting(progress = {
                        brsProgress.progress = it
                    }, receivedError = {
                        it?.let { snackbar(this, it, Snackbar.LENGTH_LONG)?.show() }
                    }, sslError = {
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
                    })
                    settings.userAgentString = Config.USER_AGENT

                    loadUrl(url)
                }

                brsUrlBar.run {
                    val brsUrlBarY = getActionBarHeight()
                    translationY = brsUrlBarY * -1
                    animate().translationY(0f).setDuration(400).start()
                }
            }

            settingEvents(it)
        }
    }

    private fun settingEvents(url: String) = viewmodel.run {
        applyUrl(url)
        applyBrsCount(mBinding.brsArea.childCount)

        observe(backEvent) { onBackPressed() }
        observe(reloadEvent) { mBinding.brsWebview.reload() }
    }

    override fun onBackPressed(): Boolean {
        mBinding.run {
            if (brsWebview.canGoBack()) {
                brsWebview.goBack()
            } else {
                // back 처리를 activity 에 전달
                return false
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

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserFragment
    }
}
