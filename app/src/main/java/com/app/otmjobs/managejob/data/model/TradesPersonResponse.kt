package com.app.otmjobs.managejob.data.model

import com.app.otmjobs.common.data.model.BaseResponse

class TradesPersonResponse : BaseResponse() {
    var info: MutableList<TradePersonInfo> = ArrayList()
}