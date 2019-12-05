package brigitte.widget

import android.view.MotionEvent

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-22 <p/>
 */

inline fun magneticEffect(event: MotionEvent, y: Int, max: Int, callback: (Boolean) -> Unit): Boolean {
    val mid = max / 2

    return when (event.action) {
        MotionEvent.ACTION_UP -> {
            val result = when (y) {
                in mid..-1 -> {
                    callback.invoke(true)
                    true
                }
                in (max + 1) until mid -> {
                    callback.invoke(false)
                    true
                }
                else -> false
            }

            result
        }
        else -> false
    }
}