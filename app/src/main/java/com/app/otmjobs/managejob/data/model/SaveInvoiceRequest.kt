package com.app.otmjobs.managejob.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SaveInvoiceRequest(
    var job_id: String = "",
    var account_name: String = "",
    var account_number: String = "",
    var sort_code: String = "",
    var vat_number: String = "",
    var vat: String = "",
    var phone_extension_id: String = "",
    var phone_number: String = "",
    var post_code: String = "",
    var address: String = "",
    var items: MutableList<JobItemInfo>? = ArrayList(),
    var device_token: String = "",
    var job_invoice_id: String = "",
    var invoice_name: String = "",
    var invoice_image: String = "",
    var invoice_pdf: String = ""
) : Parcelable