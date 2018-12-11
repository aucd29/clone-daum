package com.example.clone_daum.ui.search

import android.app.Application
import androidx.recyclerview.widget.RecyclerView
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.PopularKeyword
import com.example.clone_daum.model.remote.DaumService
import com.example.common.RecyclerViewModel
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

class PopularViewModel @Inject constructor(
    app: Application,
    val db: DbRepository,
    val disposable: CompositeDisposable
): RecyclerViewModel<PopularKeyword>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(PopularViewModel::class.java)
    }

    fun init() {
        initAdapter("search_recycler_popular_item")

        items.set(db.popularKeywordDao.list().blockingFirst())
//        items.set(arrayListOf(PopularKeyword(1, "대전 교사 투신")
//            , PopularKeyword(2, "마닷 가족 잠적")
//            , PopularKeyword(3, "분위기 좋은 레스토랑")
//            , PopularKeyword(4, "홍수현 집공개")
//            , PopularKeyword(5, "차량용공기청정기")
//        ))
    }

    fun reloadData() {
        disposable.add(db.popularKeywordDao.list().subscribe { items.set(it) })
    }

    fun event(keyword: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("EVENT : $keyword")
        }
    }
}