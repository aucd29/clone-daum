package brigitte.widget.pageradapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-22 <p/>
 */

@SuppressLint("WrongConstant")
abstract class FragmentStatePagerAdapter(
    fm: FragmentManager
) : androidx.fragment.app.FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)


abstract class FragmentPagerAdapter(
    fm: FragmentManager
) : androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)


abstract class PagerAdapter constructor (
) : androidx.viewpager.widget.PagerAdapter() {
    override fun isViewFromObject(view: View, obj: Any) = view == obj
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        if (obj is View) container.removeView(obj)
    }
}