package com.example.clone_daum.ui.search

import android.os.Bundle
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
import com.example.common.BaseRuleFragment
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
    lateinit var vmfactory: DaggerViewModelFactory

    lateinit var viewmodel: SearchViewModel

    override fun bindViewModel() {
        viewmodel = vmfactory.inject(this, SearchViewModel::class.java)

        mBinding.model = viewmodel.apply {
            initAdapter("search_recycler_history_item")

            // TODO
//            disposable.add(Repository.searchHistoryDao.search().subscribe {
//                if (mLog.isDebugEnabled) {
//                    mLog.debug("history count : ${it.size}")
//                }
//
//                setItems(it)
//            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        settingEvents()
    }

    fun settingEvents() = viewmodel.run {
        observe(closeEvent) {
            activity().supportFragmentManager.popBackStack()
        }

        observe(searchEvent) {
            // brs fragment
        }
    }

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): SearchFragment
    }
}