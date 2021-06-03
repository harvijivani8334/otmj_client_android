package com.app.otmjobs.managejob.data.remote

import com.app.otmjobs.authentication.data.model.PhoneExtensionResponse
import com.app.otmjobs.authentication.data.model.SignUpRequest
import com.app.otmjobs.authentication.data.model.UserResponse
import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.managejob.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ManageJobInterface {
    @GET("trade/get")
    suspend fun getTrades(): GetTradesResponse

    @POST("job/add")
    suspend fun addJob(@Body postJobRequest: PostJobRequest): AddJobResponse

    @POST("job/update")
    suspend fun updateJob(@Body postJobRequest: PostJobRequest): AddJobResponse

    @POST("job/delete")
    suspend fun deleteJob(@Body deleteJobRequest: DeleteJobRequest): BaseResponse

    @GET("job/get")
    suspend fun getMyJobs(): MyJobsResponse

    @GET("job/get-details")
    suspend fun getJobDetails(@Query("job_id") job_id: Int): AddJobResponse

    @Multipart
    @POST("job/image/add")
    suspend fun addJobImage(
        @Part("job_id") job_id: RequestBody,
        @Part("device_token") device_token: RequestBody,
        @Part("device_type") device_type: RequestBody,
        @Part file_name: MultipartBody.Part?
    ): AddJobResponse

    @Multipart
    @POST("job/image/delete")
    suspend fun deleteJobImage(
        @Part("job_image_id") job_image_id: RequestBody,
        @Part("device_token") device_token: RequestBody,
        @Part("device_type") device_type: RequestBody,
    ): BaseResponse
}