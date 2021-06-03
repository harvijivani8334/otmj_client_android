package com.app.otmjobs.managejob.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.otmjobs.authentication.data.model.User
import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.traceErrorException
import com.app.otmjobs.managejob.data.model.*
import com.app.otmjobs.managejob.data.repository.ManageJobRepository
import com.app.utilities.utils.StringHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import java.io.File
import java.util.concurrent.CancellationException

class ManageJobViewModel(private val manageJobRepository: ManageJobRepository) :
    ViewModel() {
    val getTradesResponse = MutableLiveData<GetTradesResponse>()
    val addJobResponse = MutableLiveData<AddJobResponse>()
    val myJobsResponse = MutableLiveData<MyJobsResponse>()
    val baseResponse = MutableLiveData<BaseResponse>()

    fun getTradesResponse() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = manageJobRepository.getTrades()
                withContext(Dispatchers.Main) {
                    getTradesResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }

    fun addJobResponse(postJobRequest: PostJobRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = manageJobRepository.addJob(postJobRequest)
                withContext(Dispatchers.Main) {
                    addJobResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }

    fun updateJobResponse(postJobRequest: PostJobRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = manageJobRepository.addJob(postJobRequest)
                withContext(Dispatchers.Main) {
                    addJobResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }

    fun deleteJobResponse(deleteJobRequest: DeleteJobRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = manageJobRepository.deleteJob(deleteJobRequest)
                withContext(Dispatchers.Main) {
                    baseResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }

    fun getMyJobsResponse() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = manageJobRepository.getMyJobs()
                withContext(Dispatchers.Main) {
                    myJobsResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }

    fun getJobDetailsResponse(jobId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = manageJobRepository.getJobDetails(jobId)
                withContext(Dispatchers.Main) {
                    addJobResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }

    fun addJobImageResponse(jobId: Int, image: String?) {
        var imageFileBody: MultipartBody.Part? = null
        val jobId: RequestBody = AppUtils.getRequestBody(jobId.toString())
        val deviceToken: RequestBody = AppUtils.getRequestBody(AppUtils.getDeviceToken())
        val deviceType: RequestBody = AppUtils.getRequestBody(AppConstants.DEVICE_TYPE.toString())

        if (!StringHelper.isEmpty(image)) {
            val file = File(image!!)
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            imageFileBody = MultipartBody.Part.createFormData("file_name", file.name, requestBody)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = manageJobRepository.addJobImage(
                    jobId, deviceToken, deviceType, imageFileBody
                )
                withContext(Dispatchers.Main) {
                    addJobResponse.value = response
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                traceErrorException(e)
            } catch (e: CancellationException) {
                e.printStackTrace()
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }

    fun deleteJobImageResponse(jobImageId: Int) {
        val jobImageId: RequestBody = AppUtils.getRequestBody(jobImageId.toString())
        val deviceToken: RequestBody = AppUtils.getRequestBody(AppUtils.getDeviceToken())
        val deviceType: RequestBody = AppUtils.getRequestBody(AppConstants.DEVICE_TYPE.toString())

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    manageJobRepository.deleteJobImage(jobImageId, deviceToken, deviceType)
                withContext(Dispatchers.Main) {
                    baseResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }
}