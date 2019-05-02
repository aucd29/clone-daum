@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.os.Looper
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import java.util.concurrent.Executors

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-04-24 <p/>
 */

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

// https://proandroiddev.com/the-ugly-onpropertychangedcallback-63c78c762394
inline fun <reified T: Observable> T.observe(noinline callback: (T) -> Unit) =
    addOnPropertyChangedCallback(
        object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) =
                callback(observable as T)
        })


inline fun validateMainThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
        throw IllegalStateException("Must be called from the main thread.")
    }
}

inline fun ObservableInt.gone() = set(View.GONE)
inline fun ObservableInt.visible() = set(View.VISIBLE)
inline fun ObservableInt.isVisible() = get() == View.VISIBLE
inline fun ObservableInt.visibleToggle() = set(if (get() == View.VISIBLE) View.GONE else View.VISIBLE)
inline fun ObservableField<String>.reset() = set("")

