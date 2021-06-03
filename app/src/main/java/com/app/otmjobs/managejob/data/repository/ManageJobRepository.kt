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
}