package com.app.otmjobs.managejob.data.model

import com.app.otmjobs.common.data.model.BaseResponse

class MyJobsResponse : BaseResponse() {
    var info: MutableList<PostJobRequest> = ArrayList()
}