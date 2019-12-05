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

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS   = 60 * MINUTE_MILLIS
private const val DAY_MILLIS    = 24 * HOUR_MILLIS

//https://stackoverflow.com/questions/35858608/how-to-convert-time-to-time-ago-in-android
fun Long.toTimeAgoString(): String {
    val now = System.currentTimeMillis()
    if (this > now || this <= 0) {
        return "in the future"
    }

    // TODO 하드코딩한 문자열은 추후 string.xml 을 통해서 변경해야 함
    val diff = now - this
    return when {
        diff < MINUTE_MILLIS      -> "moments ago"
        diff < 2 * MINUTE_MILLIS  -> "a minute ago"
        diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 2 * HOUR_MILLIS    -> "an hour ago"
        diff < 24 * HOUR_MILLIS   -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS   -> "yesterday"
//        else -> "${diff / DAY_MILLIS} days ago"
        else -> toDateString()
    }
}