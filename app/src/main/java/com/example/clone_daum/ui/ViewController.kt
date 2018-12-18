package com.example.clone_daum.ui

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.clone_daum.R
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.clone_daum.ui.browser.BrowserSubmenuFragment
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.search.SearchFragment
import com.example.common.FragmentAnim
import com.example.common.FragmentCommit
import com.example.common.FragmentParams
import com.example.common.show

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 13. <p/>
 */

class ViewController private constructor() {
    private object Holder { val INSTANCE = ViewController() }

    companion object {
        val get: ViewController by lazy { Holder.INSTANCE }

        const val CONTAINER = R.id.container
    }

    lateinit var manager: FragmentManager

    fun mainFragment() {
        manager.show(FragmentParams(CONTAINER,
            MainFragment::class.java, commit = FragmentCommit.NOW, backStack = false))
    }

    fun searchFragment() {
        manager.show(FragmentParams(CONTAINER,
            SearchFragment::class.java, anim = FragmentAnim.ALPHA))
    }

    fun browserFragment(bundle: Bundle) {
        manager.show(FragmentParams(CONTAINER,
            BrowserFragment::class.java, anim = FragmentAnim.ALPHA, bundle = bundle))
    }

    fun browserSubFragment(container: Int, subManager: FragmentManager) {
        subManager.show(FragmentParams(container,
            BrowserSubmenuFragment::class.java, anim = FragmentAnim.ALPHA))
    }
}