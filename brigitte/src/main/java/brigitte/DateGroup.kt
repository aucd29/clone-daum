@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 3. <p/>
 */


/**
 * unix timestamp 를 전달 받은 포맷에 받게 날짜로 변환 한다.
 */
fun Long.toDate(format: String): String = SimpleDateFormat(format, Locale.getDefault()).format(this)