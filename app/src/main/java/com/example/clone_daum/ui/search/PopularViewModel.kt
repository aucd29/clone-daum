package com.example.clone_daum.ui.search

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.remote.GithubService
import com.example.clone_daum.model.remote.PopularKeyword
import com.example.common.ICommandEventAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

class PopularViewModel @Inject constructor(app: Application
    , val github: GithubService
) : RecyclerViewModel<PopularKeyword>(app), ICommandEventAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(PopularViewModel::class.java)

        const val CMD_BRS_SEARCH = "brs-search"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()

    val dp                = CompositeDisposable()
    val visiablePopular   = ObservableInt(View.GONE)
    val chipLayoutManager = ObservableField<ChipsLayoutManager>()

    fun init() {
        dp.add(github.popularKeywordList()
            .subscribeOn(Schedulers.io())
            .map {
                // 서버에서 읽어오는 데이터는 List<String> 이라서 Recycler 에
                // 사용하기 위해 변환
                val convert = ArrayList<PopularKeyword>()
                it.forEach { convert.add(PopularKeyword(it)) }
                convert
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                items.set(it)
                visiablePopular.set(View.VISIBLE)
            }, { e -> mLog.error("ERROR: ${e.message}") }))

        // CHIP 레이아웃 의 아이템을 선택할 경우에 대해 처리 한다.
        initAdapter("search_recycler_popular_item")
    }
}