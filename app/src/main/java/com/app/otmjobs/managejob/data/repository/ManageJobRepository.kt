package com.app.otmjobs.managejob.data.repository

import com.app.otmjobs.authentication.data.model.UserResponse
import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.managejob.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ManageJobRepository {
    suspend fun getTrades(): GetTradesResponse
    suspend fun addJob(postJobRequest: PostJobRequest): AddJobResponse
    suspend fun updateJob(postJobRequest: PostJobRequest): AddJobResponse
    suspend fun deleteJob(deleteJobRequest: DeleteJobRequest): BaseResponse
    suspend fun getJobDetails(jobId: Int): AddJobResponse
    suspend fun getMyJobs(): MyJobsResponse
    suspend fun addJobImage(
        job_id: RequestBody,
        device_token: RequestBody,
        device_type: RequestBody,
        file_name: MultipartBody.Part?,
    ): AddJobResponse

    suspend fun deleteJobImage(
        job_image_id: RequestBody,
        device_token: RequestBody,
        device_type: RequestBody,
    ): BaseResponse

    suspend fun jobPaused(jobId: Int): BaseResponse

    suspend fun jobCompleted(jobId: Int): BaseResponse

    suspend fun getTradesPersons(jobId: Int): TradesPersonResponse

    suspend fun getTradesPersons(): TradesPersonResponse

    suspend fun getWorkerDetails(workerId: RequestBody,job_id: RequestBody): WorkDetailsResponse

    suspend fun acceptRejectJobApplication(job_application_id: Int,status_id: Int): BaseResponse

    suspend fun repostJob(job_id: Int,device_id: Int): BaseResponse

    suspend fun getActionLog(job_id: RequestBody): JobHistoryResponse

}