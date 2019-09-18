package com.example.clone_daum.ui.main.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import brigitte.widget.pageradapter.FragmentPagerAdapter
import com.example.clone_daum.ui.main.navigation.cafe.CafeFragment
import com.example.clone_daum.ui.main.navigation.mail.MailFragment
import com.example.clone_daum.ui.main.navigation.shortcut.ShortcutFragment
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-16 <p/>
 */

class NavigationTabAdapter @Inject constructor(
    fm: FragmentManager
): FragmentPagerAdapter(fm) {
    @Inject lateinit var shortcutFragment: Provider<ShortcutFragment>
    @Inject lateinit var mailFragment: Provider<MailFragment>
    @Inject lateinit var cafeFragment: Provider<CafeFragment>

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0    -> shortcutFragment.get()
            1    -> mailFragment.get()
            else -> cafeFragment.get()
        }
    }

    override fun getCount() = 3
}