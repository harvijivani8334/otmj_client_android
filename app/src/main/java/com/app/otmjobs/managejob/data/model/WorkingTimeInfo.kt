package com.app.otmjobs.managejob.data.model


data class WorkingTimeInfo(
    val day: String? = "",
    val id: Int? = 0,
    val day_number: Int? = 0,
    val status: Boolean? = false,
    val start_time: String? = "",
    val end_time: String? = "",
)