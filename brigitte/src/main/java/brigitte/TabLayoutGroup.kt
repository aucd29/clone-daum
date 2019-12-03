@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import com.google.android.material.tabs.TabLayout

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

inline val TabLayout.tabs: List<TabLayout.Tab?>
    get() = (0 until tabCount).map { getTabAt(it) }


////////////////////////////////////////////////////////////////////////////////////
//
// TabSelectedCallback
//
////////////////////////////////////////////////////////////////////////////////////

class TabSelectedCallback constructor (
    val callback: ((TabLayout.Tab?) -> Unit)? = null
): TabLayout.OnTabSelectedListener {
    override fun onTabReselected(p0: TabLayout.Tab?) { }
    override fun onTabUnselected(p0: TabLayout.Tab?) { }
    override fun onTabSelected(p0: TabLayout.Tab?) {
        callback?.invoke(p0)
    }
}
