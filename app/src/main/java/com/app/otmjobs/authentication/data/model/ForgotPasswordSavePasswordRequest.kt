package com.app.otmjobs.authentication.data.model

import com.app.otmjobs.common.data.model.BaseResponse

data class ForgotPasswordSavePasswordRequest(
    var email: String = "",
    var password: String = "",
    var confirm_password: String = "",
    var guard: String = "",
) : BaseResponse()
