@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.example.common

import android.app.Activity
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 26. <p/>
 */

inline fun Activity.popupMenu(@MenuRes resid: Int, anchor: View, noinline listener: ((MenuItem) -> Boolean)? = null): PopupMenu {
    val popup = PopupMenu(applicationContext, anchor).apply {
        menuInflater.inflate(resid, this.menu)

        listener?.let { lsr -> setOnMenuItemClickListener { lsr.invoke(it) } }
        show()
    }

    return popup
}

inline fun Fragment.popupMenu(@MenuRes resid: Int, anchor: View, noinline listener: ((MenuItem) -> Boolean)? = null) =
    requireActivity().popupMenu(resid, anchor, listener)

inline fun PopupMenu.enableAll(flag: Boolean = true) {
    val size = menu.size()
    (0..size).forEach { menu.getItem(it).isEnabled = flag }
}

inline fun PopupMenu.enable(@IdRes id: Int) {
    menu.findItem(id)?.isEnabled = true
}

inline fun PopupMenu.disable(@IdRes id: Int) {
    menu.findItem(id)?.isEnabled = false
}