package com.app.otmjobs.common.utils

import android.content.Context

object LastSeenTime {
    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS


    fun getTimeAgo(time: Long, mContext: Context): String {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }
        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return "last seen just now"
            //return null;
        }

        // TODO: localize
        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "last seen just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "last seen a minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            "last seen " + diff / MINUTE_MILLIS + " minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "last seen an hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            "last seen " + diff / HOUR_MILLIS + " hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            "last seen on yesterday"
        } else {
            "last seen " + diff / DAY_MILLIS + " days ago"
        }
    }
}