package com.app.otmjobs.authentication.data.model

import android.os.Parcelable
import com.app.otmjobs.common.data.model.BaseResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
class ForgotPasswordUserExistResponse(
    var mobile: String = "",
    var mobileWithExtension: String = "",
    var mobileExtension: String = "",
    var email: String = "",
) : BaseResponse(), Parcelable
