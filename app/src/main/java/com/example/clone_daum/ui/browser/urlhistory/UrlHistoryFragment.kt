package com.example.clone_daum.ui.browser.urlhistory

import com.example.clone_daum.databinding.UrlHistoryFragmentBinding
import com.example.common.BaseDaggerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryFragment : BaseDaggerFragment<UrlHistoryFragmentBinding, UrlHistoryViewModel>() {
    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            init(mDisposable)
            initItems()
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
        abstract fun contributeUrlHistoryFragmentInjector(): UrlHistoryFragment
    }
}
