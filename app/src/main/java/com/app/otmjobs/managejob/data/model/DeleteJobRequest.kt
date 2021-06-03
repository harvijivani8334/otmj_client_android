package com.app.otmjobs.managejob.data.model

import com.app.otmjobs.common.data.model.BaseRequest


class DeleteJobRequest(
    var job_id: Int? = 0,
) : BaseRequest()