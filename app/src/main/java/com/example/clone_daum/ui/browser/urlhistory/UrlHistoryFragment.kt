package com.example.clone_daum.ui.browser.urlhistory

import com.example.clone_daum.databinding.UrlHistoryFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.common.BaseDaggerFragment
import com.example.common.find
import com.example.common.finish
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryFragment : BaseDaggerFragment<UrlHistoryFragmentBinding, UrlHistoryViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(UrlHistoryFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            init(mDisposable)
            initItems()
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        UrlHistoryViewModel.apply {
            when (cmd) {
                CMD_BRS_OPEN -> showBrowser(data as String)
            }
        }
    }

    private fun showBrowser(url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("SHOW BROWSER $url")
        }

        finish()
        fragmentManager?.find<BrowserFragment>()?.loadUrl(url)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeUrlHistoryFragmentInjector(): UrlHistoryFragment
    }
}
