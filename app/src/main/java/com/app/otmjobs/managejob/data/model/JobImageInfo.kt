package com.app.otmjobs.managejob.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class JobImageInfo(
    var job_image_id: Int = 0,
    var job_id: Int = 0,
    var file_name: String? = null,
    var file_type: String? = null,
    var device_id: String? = null,
    var date_added: String? = null,
    var date_deleted: String? = null,
) : Parcelable