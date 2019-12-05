@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import junit.framework.TestCase

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-08 <p/>
 */

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

// 이건 너무 튄다.
infix fun <A, B> A.asserteq(that: B) = TestCase.assertEquals(this, that)

