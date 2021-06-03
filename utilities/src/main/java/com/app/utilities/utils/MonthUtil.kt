package com.app.utilities.utils

import java.util.*

object MonthUtil {
    private val monthArray = arrayOf(
        arrayOf(
            "January", "Jan"
        ), arrayOf(
            "February", "Feb"
        ), arrayOf(
            "March", "Mar"
        ), arrayOf(
            "April", "April"
        ), arrayOf(
            "May", "May"
        ), arrayOf(
            "June", "June"
        ), arrayOf(
            "July", "July"
        ), arrayOf(
            "August", "Aug"
        ), arrayOf(
            "September", "Sept"
        ), arrayOf(
            "October", "Oct"
        ), arrayOf(
            "November", "Nov"
        ), arrayOf(
            "December", "Dec"
        )
    )

    fun getShortMonthName(longName: String): String? {
        for (i in monthArray.indices) {
            if (longName == monthArray[i][0]) {
                return monthArray[i][1]
            }
        }
        return null
    }

    fun getLongMonthName(shortName: String): String? {
        for (i in monthArray.indices) {
            if (shortName == monthArray[i][1]) {
                return monthArray[i][0]
            }
        }
        return null
    }

    fun getShortMonthNameList(): List<*> {
        val monthList: MutableList<String> = ArrayList()
        for (strings in monthArray) {
            monthList.add(strings[1])
        }
        return monthList
    }

    fun getLongMonthNameList(): List<*> {
        val monthList: MutableList<String?> = ArrayList()
        for (strings in monthArray) {
            monthList.add(strings[0])
        }
        return monthList
    }
}