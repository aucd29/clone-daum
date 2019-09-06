package com.example.clone_daum.ui.search

import android.os.Bundle
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.ui.FragmentFactory
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
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

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var layoutManager: ChipsLayoutManager
    @Inject lateinit var fragmentFactory: FragmentFactory

    override val layoutId = R.layout.search_fragment

    private lateinit var mPopularViewModel: PopularViewModel

    override fun initViewBinding() {
    }

    override fun bindViewModel() {
        super.bindViewModel()

        mPopularViewModel     = inject(requireActivity())
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
        navigate(R.id.actionGlobalBrowserFragment, Bundle().apply {
            putString("url", url.toString())
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector
        abstract fun contributeInjector(): SearchFragment
    }
}