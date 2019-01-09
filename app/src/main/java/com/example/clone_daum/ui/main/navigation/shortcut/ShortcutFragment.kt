package com.example.clone_daum.ui.main.navigation.shortcut

import com.example.clone_daum.databinding.ShortcutFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import com.example.common.di.module.injectOf
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class ShortcutFragment: BaseDaggerFragment<ShortcutFragmentBinding, ShortcutViewModel>() {
    private lateinit var mSitemapViewModel : SitemapViewModel
    private lateinit var mFrequentlySiteModel : FrequentlySiteViewModel

    @Inject lateinit var viewController: ViewController

    override fun bindViewModel() {
        super.bindViewModel()

        mViewModelFactory.run {
            // sitemap, frequently 의 view model 은 shortcut fragment 내에서만 동작해야 하므로 injectOf 를 이용 한다.
            mSitemapViewModel    = injectOf(this@ShortcutFragment, SitemapViewModel::class.java)
            mFrequentlySiteModel = injectOf(this@ShortcutFragment, FrequentlySiteViewModel::class.java)
        }

        mBinding.run {
            sitemapModel        = mSitemapViewModel
            frequentlySiteModel = mFrequentlySiteModel
        }
    }

    override fun initViewBinding() {

    }

    override fun initViewModelEvents() = mViewModel.run {
        observe(brsSitemapEvent) {
            viewController.browserFragment(it)
        }

        observe(mSitemapViewModel.brsOpenEvent) {
            viewController.browserFragment(it)
        }

        observe(mFrequentlySiteModel.brsOpenEvent) {
            viewController.browserFragment(it)
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
        abstract fun contributeInjector(): ShortcutFragment
    }
}
