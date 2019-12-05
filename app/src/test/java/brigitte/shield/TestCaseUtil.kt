@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-23 <p/>
 *
 * android-architecture 참조
 * 먼가 빌드 실행 시 과거 정보가 남아서 제대로 동작 안되는 듯한 현상이 자주 일어난다.. 망했다..
 */

// https://stackoverflow.com/questions/40300469/mock-build-version-with-mockito
@Throws(Exception::class)
inline fun setFinalStatic(field: Field, newValue: Any) {
    field.isAccessible = true

    val modifiersField = Field::class.java.getDeclaredField("modifiers");
    modifiersField.isAccessible = true;
    modifiersField.setInt(field, field.getModifiers() and Modifier.FINAL.inv())

    field.set(null, newValue);
}