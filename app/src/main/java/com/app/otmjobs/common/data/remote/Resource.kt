package com.app.otmjobs.common.data.remote

import com.app.otmjobs.common.utils.AppConstants

data class Resource<out T>(val status: Int, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(AppConstants.Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(AppConstants.Status.ERROR, data, msg)
        }
    }
}