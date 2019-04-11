package com.example.common

import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 11. <p/>
 */

class CalendarTest {
    @Test
    fun calTest() {
        val cal = Calendar.getInstance()
        val sdf = cal.defaultDateFormat()
        val lastTimeToday = cal.lastTimeToday()

        cal.setYmd()
        System.out.println("ymd : ${sdf.format(cal.time)}")

        val prevWeek = cal.prevWeek()
        System.out.println("prev week : ${sdf.format(prevWeek.time)}")

        val prevMonth = cal.prevMonth()
        System.out.println("prev month : ${sdf.format(prevMonth.time)}")

        val twoMonthAgo = cal.monthAgo(-2)
        System.out.println("two month ago : ${sdf.format(twoMonthAgo.time)}")

        val testcal = Calendar.getInstance()
        testcal.set(cal.y(), cal.m(), cal.d(), 21, 0, 0)
        System.out.println("after    : ${lastTimeToday.after(testcal)}")
        System.out.println("today mil : ${lastTimeToday.timeInMillis}")
        System.out.println("testc mil : ${testcal.timeInMillis}")
        System.out.println("cal : ${lastTimeToday.timeInMillis > testcal.timeInMillis}")

        System.out.println("end")
    }

    data class DumyData(val name: String, val date: Long) : IDateCalculator {
        override fun timeInMillis() = date
    }
    open class BaseDataList {
        lateinit var name:String
        val list = arrayListOf<DumyData>()
    }
    class TodayList : BaseDataList()
    class YesterdayList : BaseDataList()
    class WeekList : BaseDataList()
    class MonthList : BaseDataList()
    class OldList : BaseDataList()

    private fun dumyData(): ArrayList<DumyData> {
        var cal = Calendar.getInstance()
        val dumyList = arrayListOf<DumyData>()
        val sdf = cal.defaultDateFormat()

        cal.apply {
            set(y(), m(), d(), 15, 0, 0)
        }

        dumyList.add(DumyData("today", cal.timeInMillis))

        cal.setDayAgo(-1)
        dumyList.add(DumyData("yesterday", cal.timeInMillis))

        cal.setDayAgo(1)
        cal.setWeekAgo(-1)
        dumyList.add(DumyData("prev week", cal.timeInMillis))
        dumyList.add(DumyData("prev week", cal.dayAgo(1).timeInMillis))
        dumyList.add(DumyData("prev week", cal.dayAgo(2).timeInMillis))
        dumyList.add(DumyData("prev week", cal.dayAgo(3).timeInMillis))
        dumyList.add(DumyData("prev week", cal.dayAgo(4).timeInMillis))
        dumyList.add(DumyData("prev week", cal.dayAgo(5).timeInMillis))
        dumyList.add(DumyData("prev week", cal.dayAgo(6).timeInMillis))
        dumyList.add(DumyData("prev week", cal.dayAgo(7).timeInMillis))

        cal.setWeekAgo(1)
        cal.setMonthAgo(-1)
        dumyList.add(DumyData("prev month", cal.timeInMillis))
        dumyList.add(DumyData("prev month", cal.dayAgo(1).timeInMillis))
        dumyList.add(DumyData("prev month", cal.dayAgo(3).timeInMillis))
        dumyList.add(DumyData("prev month", cal.dayAgo(5).timeInMillis))

        cal.setMonthAgo(-1)
        dumyList.add(DumyData("old", cal.timeInMillis))

        return dumyList
    }


    @Test
    fun dumyTest() {
        val cal = Calendar.getInstance()
        val dumyList = dumyData()
        val sdf = cal.defaultDateFormat()

        var todayList: TodayList? = null
        var yesterdayList: YesterdayList? = null
        var weekList: WeekList? = null
        var monthList: MonthList? = null
        var oldList: OldList? = null

        cal.setYmd()
        val today: Calendar = cal
        System.out.println("today : ${sdf.format(today.time)}")

        var yesterday: Calendar? = null
        var week: Calendar? = null
        var month: Calendar? = null

        dumyList.forEach {
            if (today.isLow(it.date)) {
                if (todayList == null) {
                    todayList = TodayList().apply {
                        name = "today"
                    }

                    yesterday = cal.dayAgo(-1)
                    System.out.println("yesterday : ${sdf.format(yesterday?.time)}")
                }

                todayList?.list?.add(it)
            } else if (yesterday?.run { isLow(it.date) } ?: false) {
                if (yesterdayList == null) {
                    yesterdayList = YesterdayList().apply {
                        name = "yesterday"
                    }

                    week = cal.prevWeek()
                    System.out.println("week : ${sdf.format(week?.time)}")
                }

                yesterdayList?.list?.add(it)
            } else if (week?.run { isLow(it.date) } ?: false) {
                if (weekList == null) {
                    weekList = WeekList().apply {
                        name = "week"
                    }

                    month = cal.prevMonth()
                    System.out.println("month : ${sdf.format(month?.time)}")
                }

                weekList?.list?.add(it)
            } else if (month?.run { isLow(it.date) } ?: false) {
                if (monthList == null) {
                    monthList = MonthList().apply {
                        name = "month"
                    }
                }

                monthList?.list?.add(it)
            } else {
                if (oldList == null) {
                    oldList = OldList().apply {
                        name = "old"
                    }
                }

                oldList?.list?.add(it)
            }

            System.out.println("    dumy data : ${it.date.toDateString()}")
        }

        val result = arrayListOf<BaseDataList>()
        if (todayList == null) {
            System.out.println("ERROR: todayList")
            assertTrue(true)
        } else {
            result.add(todayList!!)
        }

        if (yesterdayList == null) {
            System.out.println("ERROR: yesterdayList")
            assertTrue(true)
        } else {
            result.add(yesterdayList!!)
        }

        if (weekList == null) {
            System.out.println("ERROR: weekList")
            assertTrue(true)
        } else {
            result.add(weekList!!)
        }

        if (monthList == null) {
            System.out.println("ERROR: monthList")
            assertTrue(true)
        } else {
            result.add(monthList!!)
        }

        if (oldList == null) {
            System.out.println("ERROR: oldList")
            assertTrue(true)
        } else {
            result.add(oldList!!)
        }

        System.out.println("==")
        result.forEach {
            System.out.println("${it.name} (${it.list.size})")
            it.list.forEach {
                System.out.println(it.date.toDateString())
            }
            System.out.println("==")
        }
    }

    @Test
    fun dateCalculatorTest() {
        val cal = Calendar.getInstance()
        val dumyList = dumyData()
        val sdf = cal.defaultDateFormat()

        val calculator = DateCalculator<DumyData>()
        calculator.dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 EEE요일", Locale.getDefault())

        dumyList.forEach {
            calculator.process(it)
        }

        calculator.mapData.apply {
            val todayList = get(DateCalculator.K_TODAY)
            val yesterdayList = get(DateCalculator.K_YESTERDAY)
            val weekList = get(DateCalculator.K_WEEK)
            val monthList = get(DateCalculator.K_MONTH)
            val otherList = get(DateCalculator.K_OTHER)

            assertNotNull(todayList)
            assertNotNull(yesterdayList)
            assertNotNull(weekList)
            assertNotNull(monthList)
            assertNotNull(otherList)

            System.out.println("==")
            forEach {
                System.out.println("TYPE: ${calculator.dateFormatString(it.key)} (${it.value.size})")
                it.value.forEach {
                    System.out.println(it.date.toDateString())
                }
                System.out.println("==")
            }
        }
    }
}