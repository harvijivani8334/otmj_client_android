package com.app.otmjobs.authentication.data.model

import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.common.data.model.ModuleInfo

class CountryResponse : BaseResponse() {
    lateinit var data: MutableList<ModuleInfo>
}
