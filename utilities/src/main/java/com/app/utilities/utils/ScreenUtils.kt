package com.app.utilities.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

class ScreenUtils(var ctx: Context) {
    var metrics: DisplayMetrics
    val height: Int
        get() = metrics.heightPixels
    val width: Int
        get() = metrics.widthPixels
    val realHeight: Int
        get() = metrics.heightPixels / metrics.densityDpi
    val realWidth: Int
        get() = metrics.widthPixels / metrics.densityDpi
    val density: Int
        get() = metrics.densityDpi

    fun getScale(picWidth: Int): Int {
        val display = (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay
        val width = display.width
        var `val` = (width / picWidth).toDouble()
        `val` = `val` * 100.0
        return `val`.toInt()
    }

    init {
        val wm = ctx
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        metrics = DisplayMetrics()
        display.getMetrics(metrics)
    }
}