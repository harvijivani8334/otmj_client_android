package com.app.otmjobs.managechat.data.remote

import com.app.otmjobs.managechat.data.model.ChatUsersListResponse
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ManageChatInterface {
    @Multipart
    @POST("get-chat-user-data")
    suspend fun getChatUserData(@Part("guard") guard: RequestBody): ChatUsersListResponse
}