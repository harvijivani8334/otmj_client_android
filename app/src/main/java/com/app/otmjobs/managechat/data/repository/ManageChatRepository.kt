package com.app.otmjobs.managechat.data.repository

import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.managechat.data.model.ChatUsersListResponse
import com.app.otmjobs.managejob.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ManageChatRepository {
    suspend fun getChatUserList(
        guard: RequestBody,
    ): ChatUsersListResponse

}