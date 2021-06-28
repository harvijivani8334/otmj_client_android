package com.app.otmjobs.authentication.data.model

import com.app.otmjobs.common.data.model.BaseResponse

data class ForgotPasswordUserExistResponse(
    var mobile: String = "",
    var mobileWithExtension: String = "",
    var mobileExtension: String = "",
    var email: String = "",
) : BaseResponse()
