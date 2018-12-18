package com.example.clone_daum.ui.search

import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.common.di.module.inject
import com.example.common.*
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment: BaseDaggerFragment<SearchFragmentBinding, SearchViewModel>() {
    lateinit var popularviewVm: PopularViewModel

    override fun bindViewModel() = mBinding.run {
        super.bindViewModel()

        popularviewVm = mViewModelFactory.inject(this@SearchFragment, PopularViewModel::class.java)
        popularmodel  = popularviewVm
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