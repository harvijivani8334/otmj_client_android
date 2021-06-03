package com.app.otmjobs.authentication.data.model

data class User(
    var customer_id: Int = 0,
    var first_name: String = "",
    var last_name: String = "",
    var email_address: String = "",
    var password: String = "",
    var date_of_birth: String = "",
    var country_id: Int = 0,
    var phone_extension_id: String? = "",
    var phone_number: String = "",
    var pin_code: String = "",
    var street_address: String = "",
    var city: String = "",
    var ip_address: String = "",
    var time_zone: String = "",
    var verification_token: String? = "",
    var date_modified: String = "",
    var date_added: String = "",
    var api_token: String? = "",
    var image: String? = "",
    var device_token: String = "",
    var device_type: String = "",
)