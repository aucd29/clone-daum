package com.example.clone_daum.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.clone_daum.model.local.TabData

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 10. <p/>
 */

class MainTabAdapter constructor(fm: FragmentManager, val tabListData: List<TabData>)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val frgmt = MainWebviewFragment()
        val bundle = Bundle()
        bundle.putString("url", tabListData[position].url)

        frgmt.arguments = bundle

        return frgmt
    }

    override fun getPageTitle(position: Int): String = tabListData[position].name
    override fun getCount() = tabListData.size
}
