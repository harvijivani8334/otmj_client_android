package com.app.utilities.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import es.dmoral.toasty.Toasty


object ToastHelper {
    /**
     * @param mContext
     * @param message
     * @param duration (Toast.LENGTH_SORT ,Toast.LENGTH_LONG ,etc)
     * @param withIcon
     */
    fun success(mContext: Context, message: String, duration: Int, withIcon: Boolean) {
        when (duration) {
            Toast.LENGTH_SHORT ->
                Toasty.success(mContext, message, Toast.LENGTH_SHORT, withIcon).show()
            Toast.LENGTH_LONG ->
                Toasty.success(mContext, message, Toast.LENGTH_LONG, withIcon).show()
            else ->
                Toasty.success(mContext, message, duration, withIcon).show()

        }
    }

    fun error(mContext: Context, message: String, duration: Int, withIcon: Boolean) {
//        if (duration == Toast.LENGTH_SHORT) {
//            Toasty.error(mContext, message, Toast.LENGTH_SHORT, withIcon).show();
//        } else if (duration == Toast.LENGTH_LONG) {
//            Toasty.error(mContext, message, Toast.LENGTH_LONG, withIcon).show();
//        } else {
//            Toasty.error(mContext, message, duration, withIcon).show();
//        }
        when (duration) {
            Toast.LENGTH_SHORT ->
                Toasty.normal(mContext, message, Toast.LENGTH_SHORT).show()
            Toast.LENGTH_LONG ->
                Toasty.normal(mContext, message, Toast.LENGTH_LONG).show()
            else ->
                Toasty.normal(mContext, message, duration).show()
        }
    }

    fun info(mContext: Context, message: String, duration: Int, withIcon: Boolean) {
        when (duration) {
            Toast.LENGTH_SHORT ->
                Toasty.info(mContext, message, Toast.LENGTH_SHORT, withIcon).show()
            Toast.LENGTH_LONG ->
                Toasty.info(mContext, message, Toast.LENGTH_LONG, withIcon).show()
            else ->
                Toasty.info(mContext, message, duration, withIcon).show()
        }
    }

    fun warning(mContext: Context, message: String, duration: Int, withIcon: Boolean) {
        when (duration) {
            Toast.LENGTH_SHORT ->
                Toasty.warning(mContext, message, Toast.LENGTH_SHORT, withIcon).show()
            Toast.LENGTH_LONG ->
                Toasty.warning(mContext, message, Toast.LENGTH_LONG, withIcon).show()
            else ->
                Toasty.warning(mContext, message, duration, withIcon).show()
        }
    }

    fun normal(mContext: Context, message: String, duration: Int, withIcon: Boolean) {
        when (duration) {
            Toast.LENGTH_SHORT ->
                Toasty.normal(mContext, message, Toast.LENGTH_SHORT).show()
            Toast.LENGTH_LONG ->
                Toasty.normal(mContext, message, Toast.LENGTH_LONG).show()
            else ->
                Toasty.normal(mContext, message, duration).show()
        }
    }

    fun custom(
        mContext: Context,
        message: String,
        icon: Drawable?,
        duration: Int,
        withIcon: Boolean
    ) {
        when (duration) {
            Toast.LENGTH_SHORT ->
                Toasty.custom(mContext, message, icon, Toast.LENGTH_SHORT, withIcon).show()
            Toast.LENGTH_LONG ->
                Toasty.custom(mContext, message, icon, Toast.LENGTH_LONG, withIcon).show()
            else ->
                Toasty.custom(mContext, message, icon, duration, withIcon).show()
        }
    }

    fun toastyConfig(
        errorColor: Int, infoColor: Int,
        successColor: Int, textColor: Int,
        textSize: Int, tintIcon: Boolean
    ) {
        Toasty.Config.getInstance()
            .setErrorColor(errorColor)
            .setInfoColor(infoColor)
            .setSuccessColor(successColor)
            .setTextColor(textColor)
            .setTextSize(textSize)
            .tintIcon(tintIcon)
            .apply()
    }

    fun toastyConfigReset() {
        Toasty.Config.reset()
    }
}