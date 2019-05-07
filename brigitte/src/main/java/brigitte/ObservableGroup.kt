@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import androidx.databinding.ObservableBoolean

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-04-26 <p/>
 */

inline fun ObservableBoolean.toggle() {
    set(!get())
}