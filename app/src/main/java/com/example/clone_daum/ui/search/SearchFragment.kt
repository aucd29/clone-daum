package com.example.clone_daum.ui.search

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import com.example.common.di.module.injectOfActivity
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment: BaseDaggerFragment<SearchFragmentBinding, SearchViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var layoutManager: ChipsLayoutManager
    @Inject lateinit var viewController: ViewController

    private lateinit var mPopularViewModel: PopularViewModel

    override fun bindViewModel() {
        super.bindViewModel()

        mPopularViewModel     = mViewModelFactory.injectOfActivity(this@SearchFragment, PopularViewModel::class.java)
        mBinding.popularmodel = mPopularViewModel
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.init(mDisposable)
        mPopularViewModel.apply {
            init()
            chipLayoutManager.set(layoutManager)

            observe(commandEvent) { onCommandEvent(it.first, it.second) }
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
        mViewModel.finish()

        when (cmd) {
            SearchViewModel.CMD_BRS_OPEN    -> viewController.browserFragment(data.toString())
            PopularViewModel.CMD_BRS_SEARCH -> showBrowser(data.toString())
        }
    }

    private fun showBrowser(url: String) =
        viewController.browserFragment("https://m.search.daum.net/search?w=tot&q=${url.urlencode()}&DA=13H")

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): SearchFragment
    }
}