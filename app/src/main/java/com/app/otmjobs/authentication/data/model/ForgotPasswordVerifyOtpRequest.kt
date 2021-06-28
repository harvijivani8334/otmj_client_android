package com.app.otmjobs.authentication.data.model

import com.app.otmjobs.common.data.model.BaseResponse

data class ForgotPasswordVerifyOtpRequest(
    var email: String = "",
    var otp: String = "",
    var guard: String = "",
) : BaseResponse()
