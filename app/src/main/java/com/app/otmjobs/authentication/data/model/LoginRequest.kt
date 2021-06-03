package com.app.otmjobs.authentication.data.model

data class LoginRequest(
    var email: String = "",
    var password: String = "",
    var device_type: String = "",
    var device_token: String = "",
)