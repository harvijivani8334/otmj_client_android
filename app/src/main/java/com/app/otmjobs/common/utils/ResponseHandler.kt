package com.app.otmjobs.common.utils

import com.app.otmjobs.common.data.remote.Resource
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

open class ResponseHandler {
    private val BAD_REQUEST_ERROR_MESSAGE = "Bad Request!"
    private val FORBIDDEN_ERROR_MESSAGE = "Forbidden!"
    private val NOT_FOUND_ERROR_MESSAGE = "Not Found!"
    private val METHOD_NOT_ALLOWED_ERROR_MESSAGE = "Method Not Allowed!"
    private val CONFLICT_ERROR_MESSAGE = "Conflict!"
    private val UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized!"
    private val INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server error!"
    private val NO_CONNECTION_ERROR_MESSAGE = "No Connection!"
    private val TIMEOUT_ERROR_MESSAGE = "Time Out!"
    private val UNKNOWN_ERROR_MESSAGE = "Unknown Error!"

    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR_MESSAGE, null)
            is IOException -> Resource.error(NO_CONNECTION_ERROR_MESSAGE, null)
            else -> Resource.error(UNKNOWN_ERROR_MESSAGE, null)
        }
    }

    fun getErrorMessage(code: Int): String {
        return when (code) {
            400 -> BAD_REQUEST_ERROR_MESSAGE
            401 -> UNAUTHORIZED_ERROR_MESSAGE
            403 -> FORBIDDEN_ERROR_MESSAGE
            404 -> NOT_FOUND_ERROR_MESSAGE
            405 -> METHOD_NOT_ALLOWED_ERROR_MESSAGE
            409 -> CONFLICT_ERROR_MESSAGE
            500 -> INTERNAL_SERVER_ERROR_MESSAGE
            else -> UNKNOWN_ERROR_MESSAGE
        }
    }
}