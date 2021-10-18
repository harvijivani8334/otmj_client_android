package com.app.otmjobs.managechat.data.repository.imp

import com.app.otmjobs.managechat.data.model.ChatUsersListResponse
import com.app.otmjobs.managechat.data.remote.ManageChatInterface
import com.app.otmjobs.managechat.data.repository.ManageChatRepository
import okhttp3.RequestBody

class ManageChatRepositoryImp(
    private val manageChatInterface: ManageChatInterface
) : ManageChatRepository {
    override suspend fun getChatUserList(guard: RequestBody): ChatUsersListResponse {
        return manageChatInterface.getChatUserData(guard)
    }
}
