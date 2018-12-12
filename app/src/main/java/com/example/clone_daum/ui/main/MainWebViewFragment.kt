package com.example.clone_daum.ui.main

import android.os.Build
import android.os.Bundle
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.Config
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.common.*
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

class MainWebviewFragment: BaseRuleFragment<MainWebviewFragmentBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainWebviewFragment::class.java)
    }

    override fun bindViewModel() {
        mBinding.model = viewmodel()
    }

    private fun viewmodel() = viewModel(MainViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val url = arguments?.getString("url")
        val webview = mBinding.webview
        val swipeRefresh = mBinding.swipeRefresh

        if (mLog.isDebugEnabled) {
            mLog.debug("webview load url : $url")
        }

        webview.run {
            defaultSetting(urlLoading = { v, url ->
                if (mLog.isInfoEnabled) {
                    mLog.info("url : $url")
                }

                url?.let {
                    if (!it.contains("m.daum.net")) {
                        val bundle = Bundle()
                        bundle.putString("url", it)

                        activity().supportFragmentManager.add(FragmentParams(
                            R.id.container, BrowserFragment::class.java,
                            anim = FragmentAnim.ALPHA, bundle = bundle))
                    } else {
                        v?.loadUrl(url)
                    }
                }
            }, pageFinished = {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PAGE FINISHED")
                }

                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false   // hide refresh icon

                    if (mLog.isDebugEnabled) {
                        mLog.debug("HIDE REFRESH ICON")
                    }
                }
            })

            settings.userAgentString = Config.USER_AGENT

            loadUrl(url)

            // http://sarangnamu.net/basic/basic_view.php?no=6321&page=1&sCategory=0
            swipeRefresh.setOnRefreshListener {
                if (mLog.isDebugEnabled) {
                    mLog.debug("RELOAD $url")
                }
                this.reload()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding.webview.pauseTimers()
    }

    override fun onResume() {
        mBinding.webview.resumeTimers()
        super.onResume()
    }

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MainWebviewFragment
    }
}