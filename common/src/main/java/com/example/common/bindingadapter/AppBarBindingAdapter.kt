package com.example.common.bindingadapter

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
    fun bindOffsetChangedListener(appbar: AppBarLayout, callback: (AppBarLayout, Int) -> Unit) {
        appbar.addOnOffsetChangedListener(object: AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(p0: AppBarLayout, p1: Int) {
                callback(p0, p1)
            }
        })
    }

    // https://stackoverflow.com/questions/34108501/how-to-disable-scrolling-of-appbarlayout-in-coordinatorlayout
    @JvmStatic
    @BindingAdapter("bindAppBarDrag")
    fun bindAppBarDrag(appbar: AppBarLayout, state: Boolean) {
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