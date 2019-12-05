@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import junit.framework.TestCase

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-23 <p/>
 */

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
