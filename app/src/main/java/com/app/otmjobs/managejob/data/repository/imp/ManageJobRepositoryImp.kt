package com.app.otmjobs.managejob.data.repository.imp

import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.managejob.data.model.*
import com.app.otmjobs.managejob.data.remote.ManageJobInterface
import com.app.otmjobs.managejob.data.repository.ManageJobRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ManageJobRepositoryImp(
    private val manageJobInterface: ManageJobInterface
) : ManageJobRepository {
    override suspend fun getTrades(): GetTradesResponse {
        return manageJobInterface.getTrades()
    }

    override suspend fun addJob(postJobRequest: PostJobRequest): AddJobResponse {
        return manageJobInterface.addJob(postJobRequest)
    }

    override suspend fun updateJob(postJobRequest: PostJobRequest): AddJobResponse {
        return manageJobInterface.addJob(postJobRequest)
    }

    override suspend fun deleteJob(deleteJobRequest: DeleteJobRequest): BaseResponse {
        return manageJobInterface.deleteJob(deleteJobRequest)
    }

    override suspend fun getJobDetails(jobId: Int): AddJobResponse {
        return manageJobInterface.getJobDetails(jobId)
    }

    override suspend fun getMyJobs(): MyJobsResponse {
        return manageJobInterface.getMyJobs()
    }

    override suspend fun addJobImage(
        job_id: RequestBody,
        device_token: RequestBody,
        device_type: RequestBody,
        image_from: RequestBody,
        file_name: MultipartBody.Part?
    ): AddJobResponse {
        return manageJobInterface.addJobImage(job_id, device_token, device_type,image_from, file_name)
    }

    override suspend fun deleteJobImage(
        job_image_id: RequestBody,
        device_token: RequestBody,
        device_type: RequestBody
    ): BaseResponse {
        return manageJobInterface.deleteJobImage(job_image_id, device_token, device_type)
    }

    override suspend fun jobPaused(jobId: Int): BaseResponse {
        return manageJobInterface.jobPaused(jobId)
    }

    override suspend fun jobCompleted(jobId: Int): BaseResponse {
        return manageJobInterface.jobCompleted(jobId)
    }

    override suspend fun getTradesPersons(jobId: Int): TradesPersonResponse {
        return manageJobInterface.getTradesPersons(jobId)
    }

    override suspend fun getTradesPersons(): TradesPersonResponse {
        return manageJobInterface.getTradesPersons()
    }

    override suspend fun getWorkerDetails(
        workerId: RequestBody,
        job_id: RequestBody
    ): WorkDetailsResponse {
        return manageJobInterface.getWorkerDetails(workerId, job_id)
    }

    override suspend fun acceptRejectJobApplication(
        job_application_id: Int,
        status_id: Int
    ): BaseResponse {
        return manageJobInterface.acceptRejectJobApplication(job_application_id, status_id)
    }

    override suspend fun repostJob(job_id: Int, device_id: Int): BaseResponse {
        return manageJobInterface.repostJob(job_id, device_id)
    }

    override suspend fun getActionLog(job_id: RequestBody): JobHistoryResponse {
        return manageJobInterface.getActionLog(job_id)
    }

    override suspend fun getInvoices(job_id: RequestBody): GetInvoicesResponse {
        return manageJobInterface.getInvoices(job_id)
    }
}
