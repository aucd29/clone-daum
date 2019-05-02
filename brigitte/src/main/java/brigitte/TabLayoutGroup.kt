@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import com.google.android.material.tabs.TabLayout

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

inline val TabLayout.tabs: List<TabLayout.Tab?>
    get() = (0..tabCount - 1).map { getTabAt(it) }

