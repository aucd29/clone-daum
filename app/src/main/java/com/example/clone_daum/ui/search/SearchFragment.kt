package com.example.clone_daum.ui.search

import android.os.Bundle
import androidx.savedstate.SavedStateRegistryOwner
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment @Inject constructor(
) : BaseDaggerFragment<SearchFragmentBinding, SearchViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var layoutManager: ChipsLayoutManager

    override val layoutId = R.layout.search_fragment

    private val mPopularViewModel: PopularViewModel by activityInject()

    override fun initViewBinding() {
    }

    override fun bindViewModel() {
        super.bindViewModel()

        mBinding.popularmodel = mPopularViewModel
        mCommandEventModels.add(mPopularViewModel)
    }

    override fun initViewModelEvents() {
        mViewModel.init(disposable())
        mPopularViewModel.apply {
            init()
            chipLayoutManager.set(layoutManager)
        }
    }

    override fun onDestroyView() {
        mBinding.searchEdit.hideKeyboard()

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            SearchViewModel.CMD_BRS_OPEN    -> navigateBrowserFragment(data.toString())
            PopularViewModel.CMD_BRS_SEARCH -> navigateBrowserFragment(
                "https://m.search.daum.net/search?w=tot&q=${data.toString().urlencode()}&DA=13H")
        }
    }

    private fun navigateBrowserFragment(url: Any) {
        navigator.browserFragment(url.toString())
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [SearchFragmentModule::class])
        abstract fun contributeSearchFragmentInjector(): SearchFragment
    }

    @dagger.Module
    abstract class SearchFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: SearchFragment): SavedStateRegistryOwner
    }
}