package com.example.clone_daum.ui.main.navigation.shortcut

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.ShortcutFragmentBinding
import com.example.clone_daum.ui.FragmentFactory
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class ShortcutFragment @Inject constructor() : BaseDaggerFragment<ShortcutFragmentBinding, ShortcutViewModel>() {
    private val mSitemapViewModel : SitemapViewModel by inject()
    private val mFrequentlySiteModel : FrequentlySiteViewModel by inject()

    @Inject lateinit var fragmentFactory: FragmentFactory

    override val layoutId = R.layout.shortcut_fragment

    override fun bindViewModel() {
        super.bindViewModel()

        // sitemap, frequently 의 view model 은 shortcut fragment 내에서만 동작해야 하므로 injectOf 를 이용 한다.
        mBinding.apply {
            sitemapModel        = mSitemapViewModel
            frequentlySiteModel = mFrequentlySiteModel
        }
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        observe(mViewModel.brsSitemapEvent) {
            fragmentFactory.browserFragment(fragmentManager, it)
        }

        observe(mSitemapViewModel.brsOpenEvent) {
            fragmentFactory.browserFragment(fragmentManager, it)
        }

        mFrequentlySiteModel.init(disposable())
        observe(mFrequentlySiteModel.brsOpenEvent) {
            fragmentFactory.browserFragment(fragmentManager, it)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [ShortcutFragmentModule::class])
        abstract fun contributeShortcutFragmentInjector(): ShortcutFragment
    }
    
    @dagger.Module
    abstract class ShortcutFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: ShortcutFragment): SavedStateRegistryOwner
    }
}
