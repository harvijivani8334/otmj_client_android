package com.app.utilities.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    private const val TAG = "DateHelper"
    private const val LONG_DATE_FORMAT = "yyyy-MM-dd"
    const val MILLISECONDS_IN_SECOND: Long = 1000
    const val MILLISECONDS_IN_MINUTE = (1000 * 60).toLong()
    private const val MILLISECONDS_IN_HOUR = (1000 * 60 * 60).toLong()
    private const val MILLISECONDS_IN_DAY = (1000 * 3600 * 24).toLong()

    /**
     * @param dateStr
     * @param format
     * @return
     */
    fun stringToDate(dateStr: String?, format: String?): Date? {
        if (StringHelper.isEmpty(dateStr) || StringHelper.isEmpty(format))
            return null
        var d: Date? = null
        val formater = SimpleDateFormat(format)
        try {
            formater.isLenient = false
            d = formater.parse(dateStr)
        } catch (e: Exception) {
            // log.error(e);
            Log.e(TAG, "error in stringToDate() : " + e.message)
            d = null
        }
        return d!!
    }

    /**
     *
     * @param date
     * @param format
     * @return
     */
    fun dateToString(date: Date?, format: String?): String {
        var result = ""
        if (date == null || StringHelper.isEmpty(format)) return result
        val formater = SimpleDateFormat(format)
        try {
            result = formater.format(date)
        } catch (e: Exception) {
            Log.e(TAG, "error in dateToString() : " + e.message)
        }
        return result
    }

    /**
     *
     * @param format
     * @return
     */
    fun getCurrDate(format: String?): String {
        return dateToString(Date(), format)
    }

    fun getcurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun dateToMillis(date: String?, dateFormat: String?): Long {
        if (StringHelper.isEmpty(date) || StringHelper.isEmpty(dateFormat)) return 0
        val f = SimpleDateFormat(dateFormat)
        try {
            val d = f.parse(date)
            return d.time
        } catch (e: ParseException) {
            Log.e(TAG, "error in dateToMillis() : " + e.message)
        }
        return 0
    }

    fun millisToString(longDate: Long, dateFormat: String?): String {
        try {
            val calendar = Calendar.getInstance()
            //calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            calendar.timeInMillis = longDate
            val df2 = SimpleDateFormat(dateFormat)
            return df2.format(calendar.time)
        } catch (e: Exception) {
            Log.e(TAG, "error in millisToString() : " + e.message)
        }
        return ""
    }

    val currentDateInLong: Long
        get() = System.currentTimeMillis()
    val today: Int
        get() {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.DATE]
        }
    val toMonth: Int
        get() {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.MONTH] + 1
        }
    val toYear: Int
        get() {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.YEAR]
        }

    /**
     *
     * @param date
     * @return
     */
    fun getDay(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.DATE]
    }

    /**
     *
     * @param date
     * @return
     */
    fun getYear(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.YEAR]
    }

    /**
     *
     * @param date
     * @return
     */
    fun getMonth(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.MONTH] + 1
    }

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
    fun dayDiff(date1: Date, date2: Date): Long {
        return (date2.time - date1.time) / 86400000
    }

    /**
     *
     * @param before
     * @param after
     * @return
     */
    fun yearDiff(before: String?, after: String?): Int {
        val beforeDay = stringToDate(before, LONG_DATE_FORMAT)
        val afterDay = stringToDate(after, LONG_DATE_FORMAT)
        return getYear(afterDay) - getYear(beforeDay)
    }

    /**
     *
     * @param after
     * @return
     */
    fun yearDiffCurr(after: String?): Int {
        val beforeDay = Date()
        val afterDay = stringToDate(after, LONG_DATE_FORMAT)
        return getYear(beforeDay) - getYear(afterDay)
    }

    /**
     *
     * @param before
     * @return
     */
    fun dayDiffCurr(before: String?): Long {
        val currDate = stringToDate(getCurrDate(LONG_DATE_FORMAT), LONG_DATE_FORMAT)
        val beforeDate = stringToDate(before, LONG_DATE_FORMAT)
        return (currDate!!.time - beforeDate!!.time) / 86400000
    }

    /**
     *
     * @param date
     * @param day
     * @return
     */
    fun nextDay(date: Date?, day: Int): Date {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        cal.add(Calendar.DAY_OF_YEAR, day)
        return cal.time
    }

    /**
     *
     * @param date
     * @param week
     * @return
     */
    fun nextWeek(date: Date?, week: Int): Date {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        cal.add(Calendar.WEEK_OF_MONTH, week)
        return cal.time
    }

    /**
     *
     * @param date
     * @param months
     * @return
     */
    fun nextMonth(date: Date?, months: Int): Date {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        cal.add(Calendar.MONTH, months)
        return cal.time
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    fun getFirstWeekdayOfMonth(year: Int, month: Int): Int {
        val c = Calendar.getInstance()
        c.firstDayOfWeek = Calendar.SATURDAY
        c[year, month - 1] = 1
        return c[Calendar.DAY_OF_WEEK]
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    fun getLastWeekdayOfMonth(year: Int, month: Int): Int {
        val c = Calendar.getInstance()
        c.firstDayOfWeek = Calendar.SATURDAY
        c[year, month - 1] = getDaysOfMonth(year, month)
        return c[Calendar.DAY_OF_WEEK]
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    fun getDaysOfMonth(year: String, month: String): Int {
        var days = 0
        days =
            if (month == "1" || month == "3" || month == "5" || month == "7" || month == "8" || month == "10" || month == "12") {
                31
            } else if (month == "4" || month == "6" || month == "9" || month == "11") {
                30
            } else {
                if (year.toInt() % 4 == 0 && year.toInt() % 100 != 0
                    || year.toInt() % 400 == 0
                ) {
                    29
                } else {
                    28
                }
            }
        return days
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    fun getDaysOfMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     *
     * @param format
     * @return
     */
    fun getFirstDayOfMonth(format: String?): String {
        val cal = Calendar.getInstance()
        cal[Calendar.DATE] = 1
        return dateToString(cal.time, format)
    }

    /**
     *
     * @param format
     * @return
     */
    fun getLastDayOfMonth(format: String?): String {
        val cal = Calendar.getInstance()
        cal[Calendar.DATE] = 1
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.DATE, -1)
        return dateToString(cal.time, format)
    }

    /**
     *
     * @param dateOne
     * @param dateTwo
     * @return
     */
    fun isSameDay(dateOne: Date?, dateTwo: Date?): Boolean {
        if (dateOne == null || dateTwo == null) {
            return false
        }
        val milliDiff = dateTwo.time - dateOne.time
        val milliDiffAbsolute = if (milliDiff < 0) milliDiff * -1 else milliDiff
        return if (milliDiffAbsolute > MILLISECONDS_IN_DAY) {
            // more than one day difference
            false
        } else {
            // less than 24 hours difference. more checking required
            val calOne = GregorianCalendar.getInstance()
            calOne.time = dateOne
            val calOneDate = calOne[Calendar.DATE]
            calOne.add(Calendar.MILLISECOND, milliDiff.toInt())
            val calTwoDate = calOne[Calendar.DATE]
            calOneDate == calTwoDate
        }
    }

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
    fun isDay1BeforeDay2(date1: Date, date2: Date): Boolean {
        return !isSameDay(date1, date2) && date1.time < date2.time
    }

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
    fun isDay1AfterDay2(date1: Date, date2: Date): Boolean {
        return !isSameDay(date1, date2) && date1.time > date2.time
    }

    fun isTimeValidate(inTime: String?, outTime: String?, sdf_format: String?): Boolean {
        val startTime: Date
        val endTime: Date
        val sdf = SimpleDateFormat(sdf_format)
        try {
            startTime = sdf.parse(inTime)
            endTime = sdf.parse(outTime)
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
        return if (startTime == endTime) {
            false
        } else !endTime.before(startTime)
    }

    fun changeDateFormat(date: String?, dateFormatter: String?, newDateFormatter: String?): String {
        if (!StringHelper.isEmpty(date)) {
            try {
                val format = SimpleDateFormat(dateFormatter)
                val parsedDate = format.parse(date)
                val sdf1 = SimpleDateFormat(newDateFormatter)
                return sdf1.format(parsedDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }
}