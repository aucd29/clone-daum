package com.example.clone_daum.ui.browser.urlhistory

import com.example.clone_daum.databinding.UrlHistoryModifyFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryModifyFragment : BaseDaggerFragment<UrlHistoryModifyFragmentBinding, UrlHistoryModifyViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(UrlHistoryModifyFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController


    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        UrlHistoryViewModel.apply {
            when (cmd) {
                CMD_URL_HISTORY_MODIFY -> viewController.urlHistoryModifyFragment()
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
        abstract fun contributeUrlHistoryModifyFragmentInjector(): UrlHistoryModifyFragment
    }
}
