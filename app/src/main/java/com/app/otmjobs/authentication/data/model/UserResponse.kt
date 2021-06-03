package com.app.otmjobs.authentication.data.model

import com.app.otmjobs.common.data.model.BaseResponse

data class UserResponse(
    val info: User
) : BaseResponse()
