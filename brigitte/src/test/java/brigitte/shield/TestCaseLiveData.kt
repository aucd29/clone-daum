@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import junit.framework.TestCase
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-23 <p/>
 */

inline fun <T> LiveData<T>.assertNotNull() {
    TestCase.assertNotNull(value)
}

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