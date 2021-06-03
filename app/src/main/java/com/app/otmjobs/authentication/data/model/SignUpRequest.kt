package com.app.otmjobs.authentication.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SignUpRequest(
    var first_name: String = "",
    var last_name: String = "",
    var email_address: String = "",
    var password: String = "",
    var confirm_password: String = "",
    var phone_extension_id: String = "",
    var country_id: Int = 0,
    var phone_number: String = "",
    var street_address: String = "",
    var city: String = "",
    var pin_code: String = "",
    var device_type: String = "",
    var device_token: String = "",
) : Parcelable