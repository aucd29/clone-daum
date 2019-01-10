package com.example.clone_daum.ui.main.realtimeissue

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.MainTabAdapter
import com.example.common.RecyclerViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 8. <p/>
 *
 * ApiHub.class 에 RealTimeIssueService 가 존재하는데 보이질 않네 그려
 */

class RealtimeIssueViewModel @Inject constructor(app: Application
    , val preConfig: PreloadConfig
    , val disposable: CompositeDisposable
) : RecyclerViewModel<RealtimeIssue>(app) {

    companion object {
        const val K_ISSUE_ALL   = "전체 이슈검색어"
        const val K_ISSUE_NEWS  = "뉴스 이슈검색어"
        const val K_ISSUE_ENTER = "연예 이슈검색어"
        const val K_ISSUE_SPORT = "스포츠 이슈검색어"
    }

    val tabAdapter  = ObservableField<RealtimeIssueTabAdapter>()
    val viewpager   = ObservableField<ViewPager>()

    // viewpager 에 adapter 가 set 된 이후 시점을 알려줌 (ViewPagerBindingAdapter)
    val viewpagerLoadedEvent = ObservableField<() -> Unit>()
    val viewpagerPageLimit   = ObservableInt(4)

    fun type(type: String) {
        initAdapter("realtime_issue_child_item")
        when (type) {
            K_ISSUE_ALL,
            K_ISSUE_ENTER,
            K_ISSUE_NEWS,
            K_ISSUE_SPORT -> items.set(preConfig.realtimeIssueMap.get(type))
        }
    }
}