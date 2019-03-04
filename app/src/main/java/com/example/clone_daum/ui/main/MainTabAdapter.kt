package com.example.clone_daum.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.clone_daum.model.local.TabData
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 10. <p/>
 *
 * infinite
 * - https://github.com/sanyuzhang/CircularViewPager
 */

class MainTabAdapter constructor(fm: FragmentManager
    , private val mTabListData: List<TabData>)
: FragmentStatePagerAdapter(fm) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainTabAdapter::class.java)

        const val K_URL      = "url"
        const val K_POSITION = "position"
    }

    override fun getItem(position: Int): Fragment {
        val frgmt = MainWebviewFragment().apply {
            arguments = Bundle().apply {
                if (mLog.isDebugEnabled) {
                    mLog.debug("TAB URL ($position)")
                }
                putInt(K_POSITION, position)
            }
        }

        return frgmt
    }

    override fun getPageTitle(position: Int) = title(position)
    override fun getCount() = mTabListData.size // Integer.MAX_VALUE

//    private fun url(pos: Int) = data(pos).url
    private fun title(pos: Int) = data(pos).name

    private inline fun data(pos: Int) = mTabListData[pos]
}
