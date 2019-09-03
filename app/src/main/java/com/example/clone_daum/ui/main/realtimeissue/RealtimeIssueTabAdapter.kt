package com.example.clone_daum.ui.main.realtimeissue

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import brigitte.di.dagger.module.ChildFragmentManager
import brigitte.widget.pageradapter.FragmentStatePagerAdapter
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.MainFragment
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 9. <p/>
 *
 * rounded dialog
 * - https://gist.github.com/ArthurNagy/1c4a64e6c8a7ddfca58638a9453e4aed
 *
 * 디자인이 변경됨 =_ = [aucd29][2019. 2. 22.]
 */

////////////////////////////////////////////////////////////////////////////////////
//
// RealtimeIssueTabAdapter
//
////////////////////////////////////////////////////////////////////////////////////

class RealtimeIssueTabAdapter @Inject constructor(
//    @param:ChildFragmentManager("main") fm: FragmentManager
//class RealtimeIssueTabAdapter constructor(
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {
    var issueList: List<Pair<String, List<RealtimeIssue>>>? = null

    override fun getItem(position: Int): Fragment {
        val frgmt  = RealtimeIssueChildFragment()
        val bundle = Bundle()
        bundle.putInt("position", position)

        frgmt.arguments = bundle

        return frgmt
    }

    // 이슈 검색어 단어는 삭제하고 타이틀을 생성한다.
    override fun getPageTitle(position: Int) =
        issueList?.get(position)?.first ?: "ERROR"

    override fun getCount() = issueList?.size ?: 0
}
