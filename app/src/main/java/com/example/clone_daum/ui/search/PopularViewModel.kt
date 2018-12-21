package com.example.clone_daum.ui.search

import android.app.Application
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.local.PopularKeywordDao
import com.example.common.RecyclerViewModel
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

class PopularViewModel @Inject constructor(app: Application
    , val popularKeywordDao: PopularKeywordDao
): RecyclerViewModel<PopularKeyword>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PopularViewModel::class.java)
    }

    val chipLayoutManager = ObservableField<ChipsLayoutManager>()

    fun init() {
        initAdapter("search_recycler_popular_item")
        items.set(popularKeywordDao.list().blockingFirst())
    }

    fun event(keyword: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("EVENT : $keyword")
        }
    }
}