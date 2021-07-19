package com.app.otmjobs.managechat.data.model

import java.util.*

class MessageInfo(
    var content: String = "",
    var sender_id: String = "",
    var ind_delete: MutableList<String> = ArrayList(),
    var timestamp: Date? = null,
    var file: FileInfo? = null,
    var seen: HashMap<String, Date?>? = HashMap(),
    var replyMessage: ReplayMessageInfo? = null,
    var isSelected: Boolean = false
)