package com.app.otmjobs.common.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FileDetail(
    var id: Int = 0,
    var displayName: String? = null,
    var fileSize: String? = null,
    var fileDate: String? = null,
    var filePath: String? = null,
    var fileType: String? = null,
    var action: String? = null,
    var thumb: String? = null,
    var fileSizeDouble: Long? = 0,
    var isTick: Boolean = false,
    var isItemSelected: Boolean = false
) : Parcelable