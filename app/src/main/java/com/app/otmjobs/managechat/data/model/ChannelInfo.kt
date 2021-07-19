package com.app.otmjobs.managechat.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
class ChannelInfo(
    var roomId: String? = "",
    var jobCode: String? = "",
    var roomName: String? = "",
    var lastUpdated: Date? = null,
    var typingUsers: MutableList<String> = ArrayList(),
    var users: MutableList<String> = ArrayList(),
    var count: Int? = 0,
) : Parcelable