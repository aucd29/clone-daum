package com.example.clone_daum.ui.main.navigation.shortcut

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.ShortcutFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class ShortcutFragment @Inject constructor(
) : BaseDaggerFragment<ShortcutFragmentBinding, ShortcutViewModel>() {
    override val layoutId = R.layout.shortcut_fragment

    private val sitemapViewModel : SitemapViewModel by inject()
    private val frequentlySiteViewModel : FrequentlySiteViewModel by inject()

    @Inject lateinit var navigator: Navigator

    override fun bindViewModel() {
        super.bindViewModel()

        // sitemap, frequently 의 view model 은 shortcut fragment 내에서만 동작해야 하므로 injectOf 를 이용 한다.
        binding.apply {
            sitemapModel        = sitemapViewModel
            frequentlySiteModel = frequentlySiteViewModel
        }

        addCommandEventModels(sitemapViewModel, frequentlySiteViewModel)
    }

    override fun initViewBinding() {

    }

    override fun initViewModelEvents() {
        sitemapViewModel.initAdapter(R.layout.sitemap_item)

        observe(viewModel.brsSitemapEvent) {
            navigator.browserFragment(it)
        }

        frequentlySiteViewModel.load(disposable())
        observe(frequentlySiteViewModel.brsOpenEvent) {
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
    }
}
