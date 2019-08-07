@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import brigitte.CommandEventViewModel
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.mockito.Mockito.*
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.net.SocketException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-23 <p/>
 *
 * android-architecture 참조
 * 먼가 빌드 실행 시 과거 정보가 남아서 제대로 동작 안되는 듯한 현상이 자주 일어난다.. 망했다..
 */

@Throws(InterruptedException::class)
inline fun <T> awaitObserveValue(livedata: LiveData<T>, timeout: Long = 1): T? {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object: Observer<T> {
        override fun onChanged(t: T) {
            data = t

            latch.countDown()
            livedata.removeObserver(this)
        }
    }

    livedata.observeForever(observer)
    latch.await(timeout, TimeUnit.SECONDS)

    return data
}

inline fun CommandEventViewModel.testCommand(changedValues: Array<Pair<String, Any>>, process: () -> Unit) {
    val observer = mock(Observer::class.java) as Observer<Pair<String, Any>>
    commandEvent.observeForever(observer)

    process.invoke()

    changedValues.forEach {
        verify(observer).onChanged(it)
    }

    commandEvent.removeObserver(observer)
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

//
////https://gist.github.com/dpmedeiros/7f7724fdf13fc5390bb05958448cdcad
//object AndroidMockUtil {
//    private val mMainThread = Executors.newSingleThreadScheduledExecutor()
//
//    @Throws(Exception::class)
//    fun mockMainThreadHandler() {
//        val mainThreadLooper = mock(Looper::class.java)
//        val mainThreadHandler = mock(Handler::class.java)
//
//        `when`(Looper.getMainLooper()).thenReturn(mainThreadLooper)
//
//        val handlerPostAnswer = Answer {
//            val runnable = it.getArgument<Runnable>(0)
//            var delay: Long = 0L
//
//            if (it.arguments.size > 1) {
//                delay = it.getArgument(1)
//            }
//
//            if (runnable != null) {
//                mMainThread.schedule(runnable, delay, TimeUnit.MILLISECONDS)
//            }
//
//            true
//        }
//
//        doAnswer(handlerPostAnswer).`when`(mainThreadHandler)
//            .post(any(Runnable::class.java))
//        doAnswer(handlerPostAnswer).`when`(mainThreadHandler)
//            .postDelayed(any(Runnable::class.java), anyLong())
//    }
//}

// https://stackoverflow.com/questions/40300469/mock-build-version-with-mockito
@Throws(Exception::class)
inline fun setFinalStatic(field: Field, newValue: Any) {
    field.isAccessible = true

    val modifiersField = Field::class.java.getDeclaredField("modifiers");
    modifiersField.isAccessible = true;
    modifiersField.setInt(field, field.getModifiers() and Modifier.FINAL.inv())

    field.set(null, newValue);
}