package com.app.otmjobs.managejob.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TradeInfo(
    val trade_id: Int? = 0,
    var trade_name: String? = "",
    val status: Int? = 0,
    val date_added: String? = "",
    val date_modified: String? = "",
    val date_deleted: String? = "",
    val icon: String? = "",
    val description: String? = "",
    val type: String? = "",
    var children: MutableList<TradeInfo>? = ArrayList()
) : Parcelable