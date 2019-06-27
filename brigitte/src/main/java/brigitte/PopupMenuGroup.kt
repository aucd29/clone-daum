@file:Suppress("NOTHING_TO_INLINE", "unused")

package brigitte

import android.app.Activity
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 26. <p/>
 */

inline fun Activity.popupMenu(@MenuRes resid: Int, anchor: View, noinline listener: ((MenuItem) -> Boolean)? = null): PopupMenu {
    return PopupMenu(this, anchor).apply {
        menuInflater.inflate(resid, menu)

        listener?.let { lsr -> setOnMenuItemClickListener { lsr.invoke(it) } }
        show()
    }
}

inline fun Fragment.popupMenu(@MenuRes resid: Int, anchor: View, noinline listener: ((MenuItem) -> Boolean)? = null) =
    requireActivity().popupMenu(resid, anchor, listener)

inline fun PopupMenu.enableAll(flag: Boolean = true) {
    val size = menu.size()
    var i = 0
    while (i < size) {
        menu.getItem(i++).isEnabled = flag
    }
}

inline fun PopupMenu.enable(@IdRes id: Int, enable: Boolean = true) {
    menu.findItem(id)?.isEnabled = enable
}
