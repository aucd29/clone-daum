package brigitte.bindingadapter

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 3. <p/>
 */

object AppBarBindingAdapter {
    @JvmStatic
    @BindingAdapter("bindOffsetChangedListener")
    fun bindOffsetChangedListener(appbar: AppBarLayout, callback: (Int, Int) -> Unit) {
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbar, verticalOffset ->
            callback(appbar.totalScrollRange, verticalOffset)
        })
    }

    // https://stackoverflow.com/questions/34108501/how-to-disable-scrolling-of-appbarlayout-in-coordinatorlayout
    @JvmStatic
    @BindingAdapter("bindAppBarDragCallback")
    fun bindAppBarDragCallback(appbar: AppBarLayout, state: Boolean) {
        val lp = appbar.layoutParams
        if (lp is CoordinatorLayout.LayoutParams) {
            val br = if (lp.behavior == null) {
                AppBarLayout.Behavior()
            } else {
                lp.behavior!!
            }

            if (br is AppBarLayout.Behavior) {
                br.setDragCallback(object: AppBarLayout.Behavior.DragCallback() {
                    override fun canDrag(p0: AppBarLayout) = state
                })
            }

            lp.behavior = br
        }
    }
}