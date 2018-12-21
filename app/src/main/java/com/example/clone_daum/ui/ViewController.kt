package com.example.clone_daum.ui

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.clone_daum.R
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.clone_daum.ui.browser.BrowserSubmenuFragment
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.main.navigation.NavigationFragment
import com.example.clone_daum.ui.search.SearchFragment
import com.example.common.*
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 13. <p/>
 */

class ViewController private constructor() {
    private object Holder { val INSTANCE = ViewController() }

    companion object {
        private val mLog = LoggerFactory.getLogger(ViewController::class.java)
        val get: ViewController by lazy { Holder.INSTANCE }

        const val CONTAINER = R.id.container
    }

    lateinit var manager: FragmentManager

    fun mainFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MAIN FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER,
            MainFragment::class.java, commit = FragmentCommit.NOW, backStack = false))
    }

    fun navigationFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("NAVIGATION FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER,
            NavigationFragment::class.java, anim = FragmentAnim.RIGHT))
    }

    fun searchFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("SEARCH FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER,
            SearchFragment::class.java, anim = FragmentAnim.ALPHA))
    }

    fun browserFragment(bundle: Bundle) {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER,
            BrowserFragment::class.java, anim = FragmentAnim.ALPHA, bundle = bundle))
    }

    fun browserSubFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER SUBMENU FRAGMENT")
        }

        manager.showDialog(BrowserSubmenuFragment(), "submenu")
    }
}