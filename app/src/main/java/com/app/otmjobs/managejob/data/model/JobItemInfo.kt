package com.app.otmjobs.managejob.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JobItemInfo(
    var title: String? = "",
    var quantity: String? = "",
    var unit: String? = "",
    var price: String? = "",
    var amount: String? = "",
) : Parcelable