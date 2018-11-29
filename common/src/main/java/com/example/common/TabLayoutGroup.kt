@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import com.google.android.material.tabs.TabLayout

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

inline val TabLayout.childList: List<TabLayout.Tab?>
    get() = (0..tabCount - 1).map { getTabAt(it) }

