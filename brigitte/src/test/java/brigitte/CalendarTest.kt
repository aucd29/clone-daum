package brigitte

import junit.framework.Assert.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 4. 11. <p/>
 */

class CalendarTest {
    companion object {
        const val EXCLUSION_TODAY       = 1
        const val EXCLUSION_YESTERDAY   = 2
        const val EXCLUSION_WEEK        = 3
        const val EXCLUSION_MONTH       = 4
        const val EXCLUSION_OLD         = 5
    }

//    @Test
    fun calTest() {
        val cal = Calendar.getInstance()
        val sdf = cal.defaultDateFormat
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
        testcal.set(cal.y, cal.m, cal.d, 21, 0, 0)
        System.out.println("after    : ${lastTimeToday.after(testcal)}")
        System.out.println("today mil : ${lastTimeToday.timeInMillis}")
        System.out.println("testc mil : ${testcal.timeInMillis}")
        System.out.println("cal : ${lastTimeToday.timeInMillis > testcal.timeInMillis}")

        System.out.println("end")
    }

    data class DumyData(val name: String, val date: Long) : IDateCalculator {
        override var timeInMillis = date
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

    private fun dumyData(exclusion: Int = 0): ArrayList<DumyData> {
        val cal = Calendar.getInstance()
        val dumyList = arrayListOf<DumyData>()
        val sdf = cal.defaultDateFormat

        cal.apply {
            set(y, m, d, 15, 0, 0)
        }

        if (exclusion != EXCLUSION_TODAY) {
            dumyList.add(DumyData("today", cal.timeInMillis))
        }

        cal.setDayAgo(-1)

        if (exclusion != EXCLUSION_YESTERDAY) {
            dumyList.add(DumyData("yesterday", cal.timeInMillis))
        }

        cal.setDayAgo(1)
        cal.setWeekAgo(-1)

        if (exclusion != EXCLUSION_WEEK) {
            dumyList.add(DumyData("prev week", cal.timeInMillis))
            dumyList.add(DumyData("prev week", cal.dayAgo(1).timeInMillis))
            dumyList.add(DumyData("prev week", cal.dayAgo(2).timeInMillis))
            dumyList.add(DumyData("prev week", cal.dayAgo(3).timeInMillis))
            dumyList.add(DumyData("prev week", cal.dayAgo(4).timeInMillis))
            dumyList.add(DumyData("prev week", cal.dayAgo(5).timeInMillis))
            dumyList.add(DumyData("prev week", cal.dayAgo(6).timeInMillis))
            dumyList.add(DumyData("prev week", cal.dayAgo(7).timeInMillis))
        }

        cal.setWeekAgo(1)
        cal.setMonthAgo(-1)

        if (exclusion != EXCLUSION_MONTH) {
            dumyList.add(DumyData("prev month", cal.timeInMillis))
            dumyList.add(DumyData("prev month", cal.dayAgo(1).timeInMillis))
            dumyList.add(DumyData("prev month", cal.dayAgo(3).timeInMillis))
            dumyList.add(DumyData("prev month", cal.dayAgo(5).timeInMillis))
        }

        cal.setMonthAgo(-1)

        if (exclusion != EXCLUSION_OLD) {
            dumyList.add(DumyData("old", cal.timeInMillis))
        }

        return dumyList
    }

//    @Test
    fun dumyTest() {
        val cal = Calendar.getInstance()
        val dumyList = dumyData()
        val sdf = cal.defaultDateFormat

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
    fun dateCalTestAll() {
        dateCalculatorTest()
    }

    @Test
    fun dateCalTestExclusionToday() {
        dateCalculatorTest(EXCLUSION_TODAY)
    }

    @Test
    fun dateCalTestExclusionYesterday() {
        dateCalculatorTest(EXCLUSION_YESTERDAY)
    }

    @Test
    fun dateCalTestExclusionWeek() {
        dateCalculatorTest(EXCLUSION_WEEK)
    }

    @Test
    fun dateCalTestExclusionMonth() {
        dateCalculatorTest(EXCLUSION_MONTH)
    }

    @Test
    fun dateCalTestExclusionOld() {
        dateCalculatorTest(EXCLUSION_OLD)
    }

    private fun keyToString(key: Int) = DateCalculator.run {when (key) {
        K_TODAY     -> "TODAY"
        K_YESTERDAY -> "YESTERDAY"
        K_WEEK      -> "WEEK"
        K_MONTH     -> "MONTH"
        else        -> "OLD"
    } }

    private fun dateCalculatorTest(exclusion: Int = 0) {
        val cal = Calendar.getInstance()
        val dumyList = dumyData(exclusion)
        val sdf = cal.defaultDateFormat

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
            val oldList = get(DateCalculator.K_OTHER)

            when (exclusion) {
                EXCLUSION_TODAY -> {
                    assertTrue(todayList?.size == 1)
                    assertTrue(yesterdayList?.size == 2)
                    assertTrue(weekList?.size == 6)
                    assertTrue(monthList?.size == 4)
                    assertTrue(oldList?.size == 1)
                }
                EXCLUSION_YESTERDAY -> {
                    assertTrue(todayList?.size == 2)
                    assertTrue(yesterdayList?.size == 1)
                    assertTrue(weekList?.size == 6)
                    assertTrue(monthList?.size == 4)
                    assertTrue(oldList?.size == 1)
                }
                EXCLUSION_WEEK -> {
                    assertTrue(todayList?.size == 1)
                    assertTrue(yesterdayList?.size == 1)
                    assertNull(weekList)
                    assertTrue(monthList?.size == 4)
                    assertTrue(oldList?.size == 1)
                }
                EXCLUSION_MONTH -> {
                    assertTrue(todayList?.size == 2)
                    assertTrue(yesterdayList?.size == 2)
                    assertTrue(weekList?.size == 6)
                    assertNull(monthList)
                    assertTrue(oldList?.size == 1)
                }
                EXCLUSION_OLD -> {
                    assertTrue(todayList?.size == 2)
                    assertTrue(yesterdayList?.size == 2)
                    assertTrue(weekList?.size == 6)
                    assertTrue(monthList?.size == 4)
                    assertNull(oldList)
                }
                else -> {
                    assertTrue(todayList?.size == 2)
                    assertTrue(yesterdayList?.size == 2)
                    assertTrue(weekList?.size == 6)
                    assertTrue(monthList?.size == 4)
                    assertTrue(oldList?.size == 1)
                }
            }

            System.out.println("==")
            forEach {
                System.out.println("${keyToString(it.key)} - ${calculator.dateFormatString(it.key)} (${it.value.size})")
                it.value.forEach {
                    System.out.println(it.date.toDateString())
                }
                System.out.println("==")
            }

            System.out.println("== END ($exclusion) == ")
        }
    }
}