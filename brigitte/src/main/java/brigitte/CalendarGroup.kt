@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.annotation.SuppressLint
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 4. 11. <p/>
 */

inline fun Calendar.setYmd() {
    set(y, m, d, 0, 0, 0)
}

inline val Calendar.y
    get() = get(Calendar.YEAR)
inline val Calendar.m
    get () = get(Calendar.MONTH)
inline val Calendar.d
    get() = get(Calendar.DATE)
inline val Calendar.h
    get() = get(Calendar.HOUR)
inline val Calendar.i
    get() = get(Calendar.MINUTE)
inline val Calendar.s
    get() = get(Calendar.SECOND)

inline val Calendar.defaultDateFormat
    get() = SimpleDateFormat("yyyy.MM.dd H:m:s", Locale.getDefault())

inline fun Calendar.cloneAdd(field: Int, value: Int): Calendar {
    val cal = this.clone() as Calendar
    cal.add(field, value)

    return cal
}

////////////////////////////////////////////////////////////////////////////////////
//
// SET
//
////////////////////////////////////////////////////////////////////////////////////

inline fun Calendar.setDayAgo(value: Int) {
    add(Calendar.DATE, value)
}

inline fun Calendar.setWeekAgo(value: Int) {
    add(Calendar.WEEK_OF_MONTH, value)
}

inline fun Calendar.setMonthAgo(value: Int) {
    add(Calendar.MONTH, value)
}

////////////////////////////////////////////////////////////////////////////////////
//
// CLONE
//
////////////////////////////////////////////////////////////////////////////////////

inline fun Calendar.dayAgo(value: Int) =
    cloneAdd(Calendar.DATE, value)

inline fun Calendar.weekAgo(value: Int) =
    cloneAdd(Calendar.WEEK_OF_MONTH, value)

inline fun Calendar.monthAgo(value: Int) =
    cloneAdd(Calendar.MONTH, value)

inline fun Calendar.prevWeek() =
    weekAgo(-1)

inline fun Calendar.nextWeek() =
    weekAgo(1)

inline fun Calendar.prevMonth() =
    monthAgo(-1)

inline fun Calendar.nextMonth() =
    monthAgo(1)

inline fun Calendar.lastTimeToday(): Calendar {
    val cal = this.clone() as Calendar
    cal.set(y, m, d, 23, 59, 59)

    return cal
}

inline fun Calendar.isLow(time: Long) = timeInMillis <= time
inline fun Calendar.isHigh(time: Long) = timeInMillis > time

inline fun Long.toDateString(format: SimpleDateFormat? = null): String = Calendar.getInstance().run {
    timeInMillis = this@toDateString
    format?.let { it.format(time) } ?: defaultDateFormat.format(time)
}

//https://stackoverflow.com/questions/20238280/date-in-to-utc-format-java
//https://stackoverflow.com/questions/6993365/convert-string-date-into-timestamp-in-android
@SuppressLint("SimpleDateFormat")
inline fun String.toUnixTime(format: String, format2: String? = null) = try {
    SimpleDateFormat(format).parse(this).time
} catch (e: Exception) {
    format2?.run { SimpleDateFormat(this@run).parse(this).time } ?: 0
}

////////////////////////////////////////////////////////////////////////////////////
//
// IDateCalculator
//
////////////////////////////////////////////////////////////////////////////////////

interface IDateCalculator {
    var timeInMillis : Long
}

////////////////////////////////////////////////////////////////////////////////////
//
// DateCalculator
//
////////////////////////////////////////////////////////////////////////////////////

class DateCalculator<T : IDateCalculator> constructor() {
    companion object {
        private val mLog = LoggerFactory.getLogger(DateCalculator::class.java)

        const val K_TODAY     = 0
        const val K_YESTERDAY = 1
        const val K_WEEK      = 2
        const val K_MONTH     = 3
        const val K_OTHER     = 4
    }

    private val mToday = Calendar.getInstance()
    private val mYesterday by lazy(LazyThreadSafetyMode.NONE) { mToday.dayAgo(-1) }
    private val mWeek      by lazy(LazyThreadSafetyMode.NONE) { mToday.prevWeek() }
    private val mMonth     by lazy(LazyThreadSafetyMode.NONE) { mToday.prevMonth() }

    var mapData = hashMapOf<Int, ArrayList<T>>()
    var dateFormat: SimpleDateFormat? = null

    init {
        initToday()
    }

    fun dateFormat(value: String) {
        dateFormat = SimpleDateFormat(value, Locale.getDefault())
    }

    fun dateFormatString(value: Int) =
        when (value) {
            K_TODAY     -> dateFormat(mToday)
            K_YESTERDAY -> dateFormat(mYesterday)
            K_WEEK      -> dateFormat(mWeek)
            K_MONTH     -> dateFormat(mMonth)
            else        -> null
        }

    fun clear() = mapData.clear()

    fun process(date: T) {
        addData(when {
                isToday(date)     -> K_TODAY
                isYesterday(date) -> K_YESTERDAY
                isWeek(date)      -> K_WEEK
                isMonth(date)     -> K_MONTH
                else              -> K_OTHER
            }, date)
    }

    private fun initToday() {
        mToday.setYmd()

        if (mLog.isTraceEnabled) {
            mLog.trace("TODAY: ${mToday.timeInMillis.toDateString()}")
        }
    }

    private inline fun addData(type: Int, date:T) {
        var list = mapData[type]
        if (list == null) {
            list = arrayListOf()
            mapData.put(type, list)
        }

        list.add(date)
    }

    private fun isToday(date: T) = isLow(mToday, date)
    private fun isYesterday(date: T) = isLow(mYesterday, date)
    private fun isWeek(date: T) = isLow(mWeek, date)
    private fun isMonth(date: T) = isLow(mMonth, date)

    private inline fun isLow(cal: Calendar?, date: T) =
        cal?.isLow(date.timeInMillis) ?: false

    private inline fun dateFormat(cal: Calendar?) = cal?.run {
        dateFormat?.format(time)
    }
}