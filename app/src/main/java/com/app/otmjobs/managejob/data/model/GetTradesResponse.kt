package com.app.otmjobs.managejob.data.model

import com.app.otmjobs.common.data.model.BaseResponse

class GetTradesResponse : BaseResponse() {
     var info: MutableList<TradeInfo> = ArrayList()
}