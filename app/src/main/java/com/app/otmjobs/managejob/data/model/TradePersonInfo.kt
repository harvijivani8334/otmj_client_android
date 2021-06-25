package com.app.otmjobs.managejob.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TradePersonInfo(
    val job_application_id: Int = 0,
    var worker_id: String? = "",
    var worker_code: String? = "",
    val job_id: Int? = 0,
    val status: Int? = 0,
    var date_added: String? = "",
    var job_title: String? = "",
    var name: String? = "",
    var image: String? = "",
    var city: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var rating: String? = "",
    var trade_name_web: String? = "",
    var status_name: String? = "",
) : Parcelable