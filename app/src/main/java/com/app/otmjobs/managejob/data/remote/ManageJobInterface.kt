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
        @Part("image_from") image_from: RequestBody,
        @Part file_name: MultipartBody.Part?
    ): AddJobResponse

    @Multipart
    @POST("job/image/delete")
    suspend fun deleteJobImage(
        @Part("job_image_id") job_image_id: RequestBody,
        @Part("device_token") device_token: RequestBody,
        @Part("device_type") device_type: RequestBody,
    ): BaseResponse

    @Multipart
    @POST("job/paused")
    suspend fun jobPaused(@Part("job_id") job_id: Int): BaseResponse

    @Multipart
    @POST("job/completed")
    suspend fun jobCompleted(@Part("job_id") job_id: Int): BaseResponse

    @Multipart
    @POST("job-application/get-all")
    suspend fun getTradesPersons(@Part("job_id") job_id: Int): TradesPersonResponse

    @POST("job-application/get-all")
    suspend fun getTradesPersons(): TradesPersonResponse

    @Multipart
    @POST("get-worker-details")
    suspend fun getWorkerDetails(
        @Part("worker_id") worker_id: RequestBody, @Part("job_id") job_id: RequestBody
    ): WorkDetailsResponse

    @Multipart
    @POST("job-application/status")
    suspend fun acceptRejectJobApplication(
        @Part("job_application_id") job_application_id: Int, @Part("status_id") status_id: Int
    ): BaseResponse

    @Multipart
    @POST("job/repost")
    suspend fun repostJob(
        @Part("job_id") job_id: Int, @Part("device_id") device_id: Int
    ): BaseResponse

    @Multipart
    @POST("get-action-log")
    suspend fun getActionLog(@Part("job_id") job_id: RequestBody): JobHistoryResponse

    @Multipart
    @POST("job-invoice/get")
    suspend fun getInvoices(
        @Part("job_id") job_id: RequestBody,
    ): GetInvoicesResponse

}