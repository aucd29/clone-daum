package com.example.clone_daum.ui.browser

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import com.example.clone_daum.Config
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserFragmentBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
import com.example.common.BaseRuleFragment
import com.example.common.OnBackPressedListener
import com.example.common.defaultSetting
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserFragment : BaseRuleFragment<BrowserFragmentBinding>(), OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserFragment::class.java)
    }

    @Inject lateinit var vmfactory: DaggerViewModelFactory

    lateinit var viewmodel: BrowserViewModel

    override fun bindViewModel() {
        viewmodel = vmfactory.inject(this, BrowserViewModel::class.java)
        mBinding.model = viewmodel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val url = arguments?.getString("url")
        url?.let {
            mBinding.brsWebview.run {
                defaultSetting()
                settings.userAgentString = Config.USER_AGENT

                loadUrl(url)
            }

            settingEvents(it)
        }
    }

    private fun settingEvents(url: String) = viewmodel.run {
        applyUrl(url)
        applyBrsCount(mBinding.brsArea.childCount)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // 따로 okhttp 로 ssl 유효성을 확인하나?
            // trust manager 넣기 귀찮은데... -_-;;;
            context?.run {
                WebView.startSafeBrowsing(this) {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("SAFE BROWSING $it")
                    }

                    sslIconResId.set(if (it)
                            R.drawable.ic_vpn_key_black_24dp
                        else
                            R.drawable.ic_vpn_key_red_24dp)
                }
            }
        }

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

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserFragment
    }
}
