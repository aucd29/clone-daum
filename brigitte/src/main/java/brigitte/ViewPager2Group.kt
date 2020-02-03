@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-01-23 <p/>
 */

interface IViewPager2Item
abstract class BaseViewPager2Adapter<T: RecyclerView.ViewHolder>
    : RecyclerView.Adapter<T>()
abstract class BaseViewPager2FragmentStateAdapter(
    manager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(manager, lifecycle) {

    constructor(fragment: Fragment)
        : this(fragment.childFragmentManager, fragment.lifecycle)

    constructor(activity: FragmentActivity)
        : this(activity.supportFragmentManager, activity.lifecycle)

}