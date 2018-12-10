package com.example.common.bindingadapter

import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 3. <p/>
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
}