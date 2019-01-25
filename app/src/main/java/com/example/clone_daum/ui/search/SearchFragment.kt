package com.example.clone_daum.ui.search

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import com.example.common.di.module.injectOf
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment: BaseDaggerFragment<SearchFragmentBinding, SearchViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var layoutManager: ChipsLayoutManager
    @Inject lateinit var viewController: ViewController

    private lateinit var mPopularViewModel: PopularViewModel

    init {
        mViewModelScope = SCOPE_FRAGMENT
    }

    override fun bindViewModel() {
        super.bindViewModel()
        initPopularViewModel()
    }

    private fun initPopularViewModel() {
        if (mLog.isDebugEnabled) {
            mLog.debug("INJECT POPULAR VIEW MODEL")
        }

        mPopularViewModel = mViewModelFactory.injectOf(this@SearchFragment, PopularViewModel::class.java)
        mBinding.popularmodel = mPopularViewModel
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() = mViewModel.run {
        init()
        initPopularViewModelEvents()
    }

    fun initPopularViewModelEvents() = mPopularViewModel.run {
        init()
        chipLayoutManager.set(layoutManager)

        observe(commandEvent) { onCommandEvent(it.first, it.second) }
    }

    override fun onCommandEvent(cmd: String, data: Any?) {
        mViewModel.finishEvent.call()

        when (cmd) {
            SearchViewModel.CMD_BRS_OPEN -> {
                viewController.browserFragment(data!!.toString())
            }

            PopularViewModel.CMD_BRS_SEARCH -> {
                val url = "https://m.search.daum.net/search?w=tot&q=${data!!.toString().urlencode()}&DA=13H"
                viewController.browserFragment(url)
            }
        }
    }

//    // fragment 종료 시 키보드도 종료 시킨다.
//    override fun finishFragmentAware() = mViewModel.run {
//        observe(finishEvent) {
//
//            finish()
//        }
//    }

    override fun onDestroyView() {
        hideKeyboard(mBinding.searchEdit)
        mPopularViewModel.dp.clear()

        super.onDestroyView()
    }

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