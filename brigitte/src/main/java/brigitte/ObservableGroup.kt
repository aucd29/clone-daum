@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.view.View
import androidx.databinding.*

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-04-26 <p/>
 */

// https://proandroiddev.com/the-ugly-onpropertychangedcallback-63c78c762394
inline fun <reified T: Observable> T.observe(noinline callback: (T) -> Unit) =
    addOnPropertyChangedCallback(
        object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) =
                callback(observable as T)
        })

inline fun ObservableBoolean.toggle() = set(!get())

inline fun ObservableInt.visible()   = set(View.VISIBLE)
inline fun ObservableInt.invisible() = set(View.INVISIBLE)
inline fun ObservableInt.gone() = set(View.GONE)
inline fun ObservableInt.isVisible() = get() == View.VISIBLE
inline fun ObservableInt.isGone() = get() == View.GONE
inline fun ObservableInt.isInvisible() = get() == View.INVISIBLE
inline fun ObservableInt.visibleToggle() = set(if (get() == View.VISIBLE) View.GONE else View.VISIBLE)

inline fun ObservableField<String>.reset() = set("")


inline fun ObservableInt.notify(value: Int) {
    if (get() == value) notifyChange() else set(value)
}

inline fun ObservableBoolean.notify(value: Boolean) {
    if (get() == value) notifyChange() else set(value)
}

inline fun <T> ObservableField<T>.notify(value: T) {
    if (get() == value) notifyChange() else set(value)
}