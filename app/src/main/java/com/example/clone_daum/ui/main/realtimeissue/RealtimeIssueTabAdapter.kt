package com.example.clone_daum.ui.main.realtimeissue

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import brigitte.widget.pageradapter.FragmentPagerAdapter
import com.example.clone_daum.model.remote.RealtimeIssue
import javax.inject.Inject
import javax.inject.Provider

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
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {
    companion object {
        const val K_POS = "position"
    }

    @Inject lateinit var fragment: Provider<RealtimeIssueChildFragment>

    var issueList: List<Pair<String, List<RealtimeIssue>>>? = null

    override fun getItem(position: Int): Fragment {
        return fragment.get().apply {
            arguments = Bundle().apply {
                putInt(K_POS, position)
            }
        }
    }

    // 이슈 검색어 단어는 삭제하고 타이틀을 생성한다.
    override fun getPageTitle(position: Int) =
        issueList?.get(position)?.first ?: "ERROR"

    override fun getCount() = issueList?.size ?: 0
}
