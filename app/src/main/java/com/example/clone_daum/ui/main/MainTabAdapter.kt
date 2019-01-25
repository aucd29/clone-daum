package com.example.clone_daum.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.clone_daum.model.local.TabData
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 10. <p/>
 *
 * infinite
 * - https://github.com/sanyuzhang/CircularViewPager
 */

class MainTabAdapter constructor(fm: FragmentManager, val tabListData: List<TabData>)
    : FragmentStatePagerAdapter(fm) {

    companion object {
        const val K_URL      = "url"
        const val K_POSITION = "position"
    }

    override fun getItem(position: Int): Fragment {
        val frgmt = MainWebviewFragment()
        frgmt.arguments = Bundle().apply {
            putString(K_URL, url(position))
            putInt(K_POSITION, position)
        }

        return frgmt
    }

    override fun getPageTitle(position: Int): String = title(position)
    override fun getCount() = tabListData.size // Integer.MAX_VALUE

    private fun url(pos: Int) = divTabList(pos).url
    private fun title(pos: Int) = divTabList(pos).name

    private inline fun divTabList(pos: Int) = tabListData[pos % tabListData.size]
}
