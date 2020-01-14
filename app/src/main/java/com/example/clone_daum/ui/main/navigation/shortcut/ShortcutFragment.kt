package com.example.clone_daum.ui.main.navigation.shortcut

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.ShortcutFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.BaseDaggerFragment
import brigitte.RecyclerAdapter
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.model.local.BrowserSubMenu
import com.example.clone_daum.model.local.FrequentlySite
import com.example.clone_daum.model.remote.Sitemap
import dagger.Binds
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class ShortcutFragment @Inject constructor(
) : BaseDaggerFragment<ShortcutFragmentBinding, ShortcutViewModel>() {
    override val layoutId = R.layout.shortcut_fragment

    private val sitemapViewModel : SitemapViewModel by inject()
    private val frequentlySiteModel : FrequentlySiteViewModel by inject()

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var sitemapAdapter: RecyclerAdapter<Sitemap>
    @Inject lateinit var frequentlyAdapter: RecyclerAdapter<FrequentlySite>

    override fun bindViewModel() {
        super.bindViewModel()

        // sitemap, frequently 의 view model 은 shortcut fragment 내에서만 동작해야 하므로 injectOf 를 이용 한다.
        binding.apply {
            sitemapModel        = sitemapViewModel
            frequentlySiteModel = frequentlySiteModel
        }

        addCommandEventModels(sitemapViewModel, frequentlySiteModel)
    }

    override fun initViewBinding() {
        sitemapAdapter.viewModel    = sitemapViewModel
        frequentlyAdapter.viewModel = frequentlySiteModel
    }

    override fun initViewModelEvents() {
        observe(viewModel.brsSitemapEvent) {
            navigator.browserFragment(it)
        }

        frequentlySiteModel.init()
        observe(frequentlySiteModel.brsOpenEvent) {
            navigator.browserFragment(it)
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

        @dagger.Module
        companion object {
            @JvmStatic
            @Provides
            fun provideBrowserSubMenuAdapter() =
                RecyclerAdapter<BrowserSubMenu>(R.layout.browser_submenu_item)

            @JvmStatic
            @Provides
            fun provideSitemapAdapter(): RecyclerAdapter<Sitemap> =
                RecyclerAdapter(R.layout.sitemap_item)

            @JvmStatic
            @Provides
            fun provideFrequentlySiteAdapter(): RecyclerAdapter<FrequentlySite> =
                RecyclerAdapter(R.layout.frequently_item)
        }
    }
}
