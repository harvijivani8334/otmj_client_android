package com.app.otmjobs.managejob.data.model

import com.app.otmjobs.common.data.model.BaseResponse

class JobHistoryResponse : BaseResponse() {
    var info: MutableList<JobHistoryInfo> = ArrayList()
}