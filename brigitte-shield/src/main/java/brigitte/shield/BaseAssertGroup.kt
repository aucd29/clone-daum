@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import junit.framework.TestCase

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-08 <p/>
 */

////////////////////////////////////////////////////////////////////////////////////
//
// DEFAULT
//
////////////////////////////////////////////////////////////////////////////////////


inline fun <T> T.assertNotNull() {
    TestCase.assertNotNull(this)
}

inline fun <T> T.assertNull() {
    TestCase.assertNull(this)
}

inline fun Boolean.assertTrue() {
    TestCase.assertTrue(this)
}

inline fun Boolean.assertFalse() {
    TestCase.assertFalse(this)
}

inline fun <T> T.assertEquals(value: T) {
    TestCase.assertEquals(this, value)
}

////////////////////////////////////////////////////////////////////////////////////
//
// ARC
//
////////////////////////////////////////////////////////////////////////////////////


inline fun <T> LiveData<T>.assertNotNull() {
    TestCase.assertNotNull(value)
}

inline fun <T> ObservableField<T>.assertNotNull() {
    TestCase.assertNotNull(get())
}


inline fun <T> ObservableField<T>.assertEquals(value: T) {
    TestCase.assertEquals(get(), value)
}

inline fun ObservableInt.assertEquals(value: Int) {
    TestCase.assertEquals(get(), value)
}

inline fun ObservableFloat.assertEquals(value: Float) {
    TestCase.assertEquals(get(), value)
}

inline fun ObservableBoolean.assertEquals(value: Boolean) {
    TestCase.assertEquals(get(), value)
}

inline fun ObservableBoolean.assertTrue() {
    TestCase.assertTrue(get())
}

inline fun ObservableBoolean.assertFalse() {
    TestCase.assertFalse(get())
}

// 이건 너무 튄다.
infix fun <A, B> A.asserteq(that: B) = TestCase.assertEquals(this, that)

