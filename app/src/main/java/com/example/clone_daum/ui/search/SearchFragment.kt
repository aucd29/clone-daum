package com.example.clone_daum.ui.search

import android.os.Bundle
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.model.Repository
import com.example.common.BaseRuleFragment
import com.example.common.viewModel
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment: BaseRuleFragment<SearchFragmentBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    private val mDisposable = CompositeDisposable()

    override fun bindViewModel() {
        mBinding.model = viewmodel()?.apply {
            initAdapter("search_recycler_history_item")

            mDisposable.add(Repository.searchHistoryDao.search().subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("history count : ${it.size}")
                }

                setItems(it)
            })
        }
    }

    private fun viewmodel() = viewModel(SearchViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        settingEvents()
    }

    fun settingEvents() = viewmodel()?.run {
        observe(closeEvent) {
            activity().supportFragmentManager.popBackStack()
        }

        observe(searchEvent) {
            // brs fragment
        }
    }

    override fun onDestroy() {
        mDisposable.clear()

        super.onDestroy()
    }
}