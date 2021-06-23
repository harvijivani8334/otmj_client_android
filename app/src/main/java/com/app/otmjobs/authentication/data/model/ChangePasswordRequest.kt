package com.app.otmjobs.authentication.data.model

data class ChangePasswordRequest(
    var old_password: String = "",
    var password: String = "",
    var guard: String = "",
    var device_type: String = "",
    var device_token: String = "",
)