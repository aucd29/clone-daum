@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 11. <p/>
 */

inline fun Calendar.setYmd() {
    set(y(), m(), d(), 0, 0, 0)
}

inline fun Calendar.y() = get(Calendar.YEAR)
inline fun Calendar.m() = get(Calendar.MONTH)
inline fun Calendar.d() = get(Calendar.DATE)
inline fun Calendar.h() = get(Calendar.HOUR)
inline fun Calendar.i() = get(Calendar.MINUTE)
inline fun Calendar.s() = get(Calendar.SECOND)

inline fun Calendar.defaultDateFormat() =
    SimpleDateFormat("yyyy-M-d H:m:s", Locale.getDefault())

inline fun Calendar.cloneAdd (field: Int, value: Int): Calendar {
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
    cal.set(y(), m(), d(), 23, 59, 59)

    return cal
}

inline fun Calendar.isLow(time: Long) = timeInMillis <= time
inline fun Calendar.isHigh(time: Long) = timeInMillis > time

inline fun Long.toDateString() = Calendar.getInstance().run {
    timeInMillis = this@toDateString
    defaultDateFormat().format(time)
}

////////////////////////////////////////////////////////////////////////////////////
//
// IDateCalculator
//
////////////////////////////////////////////////////////////////////////////////////

interface IDateCalculator {
    fun timeInMillis() : Long
}

////////////////////////////////////////////////////////////////////////////////////
//
// DateCalculator
//
////////////////////////////////////////////////////////////////////////////////////

class DateCalculator<T : IDateCalculator> {
    companion object {
        private val mLog = LoggerFactory.getLogger(DateCalculator::class.java)

        const val K_TODAY     = 0
        const val K_YESTERDAY = 1
        const val K_WEEK      = 2
        const val K_MONTH     = 3
        const val K_OTHER     = 4
    }

    private var mToday: Calendar      = Calendar.getInstance()
    private var mYesterday: Calendar? = null
    private var mWeek: Calendar?      = null
    private var mMonth: Calendar?     = null

    var mapData = hashMapOf<Int, ArrayList<T>>()
    var dateFormat: SimpleDateFormat? = null

    init {
        // 데이터가 순차적으로 들어올거라는 단순한 생각이 있었는데
        // 다 초기화 해놔야 된다는 사실을 뒤늦게 깨닳고는 init 에서 죄다 초가화 하는걸로 수정
        initToday()
        initYesterday()
        initWeek()
        initMonth()
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

    fun process(date: T) {
        addData(if (isToday(date)) {
            K_TODAY
        } else if (isYesterday(date)) {
            K_YESTERDAY
        } else if (isWeek(date)) {
            K_WEEK
        } else if (isMonth(date)) {
            K_MONTH
        } else {
            K_OTHER
        }, date)
    }

    private fun initToday() {
        mToday.setYmd()

        if (mLog.isTraceEnabled) {
            mLog.trace("TODAY: ${mToday.timeInMillis.toDateString()}")
        }
    }

    private fun initYesterday() {
        if (mYesterday == null) {
            mYesterday = mToday.dayAgo(-1)

            if (mLog.isTraceEnabled) {
                mLog.trace("YESTERDAY: ${mYesterday!!.timeInMillis.toDateString()}")
            }
        }
    }

    private fun initWeek() {
        if (mWeek == null) {
            mWeek = mToday.prevWeek()

            if (mLog.isTraceEnabled) {
                mLog.trace("WEEK: ${mWeek!!.timeInMillis.toDateString()}")
            }
        }
    }

    private fun initMonth() {
        if (mMonth == null) {
            mMonth = mToday.prevMonth()

            if (mLog.isTraceEnabled) {
                mLog.trace("MONTH: ${mMonth!!.timeInMillis.toDateString()}")
            }
        }
    }

    private inline fun addData(type: Int, date:T) {
        var list = mapData.get(type)
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
        cal?.isLow(date.timeInMillis()) ?: false

    private inline fun dateFormat(cal: Calendar?) = cal?.run {
        dateFormat?.format(time)
    }
}