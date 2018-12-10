package com.example.clone_daum.ui.main

import android.os.Build
import android.os.Bundle
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.common.BaseRuleFragment
import com.example.common.defaultSetting
import com.example.common.viewModel
import dagger.Module
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
            defaultSetting {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PAGE FINISHED")
                }

                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false   // hide refresh icon

                    if (mLog.isDebugEnabled) {
                        mLog.debug("HIDE REFRESH ICON")
                    }
                }
            }

            // at http protocol utils
            // build.version.release, Locale.getDefault().getLanguage()
            // Locale.getDefault().getCountry()
            // paramString, AppVersion.getVersion(paramContext)

            val release = Build.VERSION.RELEASE
            val country = Locale.getDefault().country
            val language = Locale.getDefault().language
            val param = "service"   // LoginActorDeleteToken
            val version = BuildConfig.VERSION_NAME
            settings.userAgentString = "DaumMobileApp (Linux; U; Android $release; $country-$language) $param/$version"

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

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MainWebviewFragment
    }
}