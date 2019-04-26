@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.os.Looper
import androidx.databinding.Observable
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
inline fun <T: Observable> T.observe(noinline callback: (T) -> Unit) =
    addOnPropertyChangedCallback(
        object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable?, i: Int) =
                callback(observable as T)
        })


inline fun validateMainThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
        throw IllegalStateException("Must be called from the main thread.")
    }
}