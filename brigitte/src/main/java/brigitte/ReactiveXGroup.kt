@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-31 <p/>
 *
 * 기본적으로 스케쥴러는 computation 으로 설정 되어 있음
 */

// TIMER

inline fun singleTimer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Single.timer(delay, unit)

inline fun timer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Observable.timer(delay, unit)

// INTERVAL

inline fun interval(period: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS, initDelay: Long = -1) =
    if (initDelay == -1L)
        Observable.interval(period, unit)
    else
        Observable.interval(initDelay, period, unit)

