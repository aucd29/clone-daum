package com.example.clone_daum.ui.search

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.common.di.module.inject
import com.example.common.*
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

    lateinit var popularviewVm: PopularViewModel

    @Inject lateinit var layoutManager: ChipsLayoutManager

    override fun bindViewModel() = mBinding.run {
        super.bindViewModel()



        popularviewVm = mViewModelFactory.inject(this@SearchFragment, PopularViewModel::class.java)
        popularviewVm.chipLayoutManager.set(layoutManager)

        popularmodel = popularviewVm
    }

    override fun settingEvents() = mViewModel.run {
        init()
        observe(searchEvent) { browserFragment(it) }

        popularEvents()
    }

    fun popularEvents() = popularviewVm.run {
        init()
    }

    override fun finishFragmentAware() = mViewModel.run {
        observe(finishEvent) {
            finish()
            hideKeyboard(mBinding.searchEdit)
        }
    }

    private fun browserFragment(url: String) {
        
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): SearchFragment
    }
}