package com.app.otmjobs.common.data.model

open class BaseResponse {
    var IsSuccess = false
    var Message: String? = null
    var ErrorCode = 0
    var user_id: String? = null
    var identity: String? = null
    var token: String? = null
}