package com.app.otmjobs.common.data.remote

import com.app.otmjobs.common.data.model.PostCodeInfo
import retrofit2.Call
import retrofit2.http.*

interface PostCodeInterface {
    @GET("{postcode}")
    fun getPostCodes(
        @Path("postcode") endpoint: String,
        @Query("format") format: String,
        @Query("lines") lines: String
    ): Call<List<PostCodeInfo>>
}