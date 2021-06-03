package com.app.otmjobs.managejob.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PostJobRequest(
    var customer_id: Int? = 0,
    var job_id: Int? = 0,
    var trade_id: Int? = 0,
    var trade_name: String? = "",
    var job_title: String? = "",
    var job_description: String? = "",
    var budget: String? = "",
    var expected_start_time: String? = "",
    var language: String? = "",
    var phone_number: String? = "",
    var phone_extension_name: String? = "",
    var street_address: String? = "",
    var country_id: Int? = 0,
    var country: String? = "",
    var city: String? = "",
    var pin_code: String? = "",
    var second_street_address: String? = "",
    var second_country_id: Int? = 0,
    var second_country: String? = "",
    var second_city: String? = "",
    var second_pincode: String? = "",
    var device_token: String? = "",
    var device_type: String? = "",
    var date_modified: String? = "",
    var date_added: String? = "",
    var images: MutableList<JobImageInfo>? = ArrayList()
) : Parcelable