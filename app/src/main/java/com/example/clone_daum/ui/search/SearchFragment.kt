package com.example.clone_daum.ui.search

import android.os.Bundle
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
import com.example.common.*
import dagger.android.ContributesAndroidInjector
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment: BaseRuleFragment<SearchFragmentBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    @Inject lateinit var disposable: CompositeDisposable
    @Inject lateinit var vmfactory: DaggerViewModelFactory

    lateinit var viewmodel: SearchViewModel
    lateinit var popularviewmodel: PopularViewModel

    override fun bindViewModel() {
        viewmodel        = vmfactory.inject(this, SearchViewModel::class.java)
        popularviewmodel = vmfactory.inject(this, PopularViewModel::class.java)

        mBinding.run {
            model        = viewmodel
            popularmodel = popularviewmodel
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        settingEvents()
        popularEvents()
    }

    fun settingEvents() = viewmodel.run {
        init()

        observe(closeEvent) {
            finish()
            hideKeyboard(mBinding.searchEdit)
        }
        observe(errorEvent) { snackbar(mBinding.root, it)?.show() }
        observe(searchEvent) {
            if (mLog.isDebugEnabled) {
                mLog.debug("SEARCH KEYWORD : $it")
            }
        }
        observeDialog(dlgEvent, disposable)
    }

    fun popularEvents() = popularviewmodel.run {
        init()
    }

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): SearchFragment
    }
}