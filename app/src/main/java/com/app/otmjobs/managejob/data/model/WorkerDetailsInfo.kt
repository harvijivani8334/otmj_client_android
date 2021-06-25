package com.app.otmjobs.managejob.data.model

import com.app.otmjobs.common.data.model.ModuleInfo


data class WorkerDetailsInfo(
    val id: Int = 0,
    var first_name: String? = "",
    var last_name: String? = "",
    val phone_extension_id: Int = 0,
    var braintree_customer_id: String? = "",
    var image: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var pin_code: String? = "",
    var address: String? = "",
    var city: String? = "",
    val country_id: Int = 0,
    var about: String? = "",
    var trade_name_web: String? = "",
    val available_after_work: Int = 0,
    var skills: MutableList<ModuleInfo>? = ArrayList(),
    var trades: MutableList<ModuleInfo>? = ArrayList(),
    var name: String? = "",
    var extension: String? = "",
    var flag_name: String? = "",
    var extension_with_name: String? = "",
    var working_area: MutableList<WorkingAreaInfo> = ArrayList(),
    var working_time: MutableList<WorkingTimeInfo> = ArrayList()
)