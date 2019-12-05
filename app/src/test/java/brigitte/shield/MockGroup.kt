@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.mockito.Mockito.*
import org.mockito.verification.VerificationMode
import java.io.IOException
import java.net.SocketException

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-06 <p/>
 *
 * https://github.com/mockito/mockito/wiki/Mockito-features-in-Korean
 *
 * bdd - http://en.wikipedia.org/wiki/Behavior_Driven_Development
 *  * give, when, then
 *
 */

inline fun <reified T> mockObserver(event: LiveData<T>): Observer<T> {
    val mockObserver = mock(Observer::class.java) as Observer<T>
    event.observeForever(mockObserver)

    return mockObserver
}

inline fun <T> T.mockReturn(value: T) {
    `when`(this).thenReturn(value)
}

inline fun <T> T.mockThrow(e: Throwable) {
    `when`(this).thenThrow(e)
}

inline fun <T> List<T>.mockReturn(value: List<T>) {
    `when`(this).thenReturn(value)
}


////////////////////////////////////////////////////////////////////////////////////
//
// Observable.OnPropertyChangedCallback
//
////////////////////////////////////////////////////////////////////////////////////

inline fun androidx.databinding.Observable.mockCallback(): androidx.databinding.Observable.OnPropertyChangedCallback {
    val mockCallback = mock(androidx.databinding.Observable.OnPropertyChangedCallback::class.java)
    addOnPropertyChangedCallback(mockCallback)

    return mockCallback
}

inline fun androidx.databinding.Observable.OnPropertyChangedCallback.verifyPropertyChanged(mode: VerificationMode = atLeastOnce()) {
    verify(this, mode).onPropertyChanged(any(androidx.databinding.Observable::class.java), anyInt())
}


inline fun mockReactiveX() {
    RxAndroidPlugins.reset()
    RxJavaPlugins.reset()

    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }

    // https://thdev.tech/android/2019/03/04/RxJava2-Error-handling/
    RxJavaPlugins.setErrorHandler { e ->
        var error = e
        if (error is UndeliverableException) {
            error = e.cause
        }
        if (error is IOException || error is SocketException) {
            // fine, irrelevant network problem or API that throws on cancellation
            return@setErrorHandler
        }
        if (error is InterruptedException) {
            // fine, some blocking code was interrupted by a dispose call
            return@setErrorHandler
        }
        if (error is NullPointerException || error is IllegalArgumentException) {
            // that's likely a bug in the application
            Thread.currentThread().uncaughtExceptionHandler
                .uncaughtException(Thread.currentThread(), error)
            return@setErrorHandler
        }
        if (error is IllegalStateException) {
            // that's a bug in RxJava or in a custom operator
            Thread.currentThread().uncaughtExceptionHandler
                .uncaughtException(Thread.currentThread(), error)
            return@setErrorHandler
        }
//        Log.w("Undeliverable exception received, not sure what to do", error)
    }
}

