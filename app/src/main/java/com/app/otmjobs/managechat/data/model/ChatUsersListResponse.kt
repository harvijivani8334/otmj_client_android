package com.app.otmjobs.managechat.data.model

import com.app.otmjobs.common.data.model.BaseResponse

class ChatUsersListResponse : BaseResponse() {
     var info: MutableList<ChatUserInfo> = ArrayList()
}