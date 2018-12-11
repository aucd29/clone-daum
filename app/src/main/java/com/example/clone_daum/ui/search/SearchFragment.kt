package com.example.clone_daum.ui.search

import android.os.Bundle
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
import com.example.common.BaseRuleFragment
import com.example.common.DialogParam
import com.example.common.observeDialog
import com.example.common.snackbar
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

    @Inject
    lateinit var disposable: CompositeDisposable
    @Inject
    lateinit var layoutManager: ChipsLayoutManager

    @Inject
    lateinit var vmfactory: DaggerViewModelFactory
    lateinit var viewmodel: SearchViewModel
    lateinit var popularviewmodel: PopularViewModel

    override fun bindViewModel() {
        viewmodel        = vmfactory.inject(this, SearchViewModel::class.java)
        popularviewmodel = vmfactory.inject(this, PopularViewModel::class.java)

        mBinding.run {
            model        = viewmodel
            popularmodel = popularviewmodel

            chipRecycler.layoutManager = layoutManager
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        settingEvents()
        popuplarEvents()
    }

    fun settingEvents() = viewmodel.run {
        init()

        observe(closeEvent) {
            activity().supportFragmentManager.popBackStack()
        }

        observe(searchEvent) {
            if (mLog.isDebugEnabled) {
                mLog.debug("SEARCH KEYWORD : $it")
            }
        }

        observe(errorEvent) {
            if (mLog.isDebugEnabled) {
                mLog.debug("SEARCHING $it")
            }

            activity().snackbar(mBinding.root, it).show()
        }

        observeDialog(dlgEvent, disposable)
    }

    fun popuplarEvents() = popularviewmodel.run {
        init()
    }

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): SearchFragment
    }
}