package com.app.otmjobs.managejob.data.model

import com.app.otmjobs.common.data.model.BaseResponse

class GetInvoicesResponse : BaseResponse() {
    var info: MutableList<SaveInvoiceRequest> = ArrayList()
}