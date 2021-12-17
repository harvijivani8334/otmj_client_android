package com.app.otmjobs.authentication.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.otmjobs.authentication.data.model.*
import com.app.otmjobs.authentication.data.repository.AuthenticationRepository
import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.traceErrorException
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

class AuthenticationViewModel(val authenticationRepository: AuthenticationRepository) :
    ViewModel() {
    val loginResponse = MutableLiveData<UserResponse>()
    val customerDetailsResponse = MutableLiveData<UserResponse>()
    val phoneExtensionResponse = MutableLiveData<PhoneExtensionResponse>()
    val countryResponse = MutableLiveData<CountryResponse>()
    val baseResponse = MutableLiveData<BaseResponse>()
    val forgotPasswordUserExistResponse = MutableLiveData<ForgotPasswordUserExistResponse>()
    val getDeviceIdResponse = MutableLiveData<GetDeviceIdResponse>()

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userResponse = authenticationRepository.login(loginRequest)
                withContext(Dispatchers.Main) {
                    loginResponse.value = userResponse
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

    fun register(signUpRequest: SignUpRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userResponse = authenticationRepository.register(signUpRequest)
                withContext(Dispatchers.Main) {
                    loginResponse.value = userResponse
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

    fun getPhoneExtensionResponse(countryCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authenticationRepository.getPhoneExtension(countryCode)
                withContext(Dispatchers.Main) {
                    phoneExtensionResponse.value = response
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

    fun getCountriesResponse() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authenticationRepository.getCountries()
                withContext(Dispatchers.Main) {
                    countryResponse.value = response
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

    fun getCustomerDetails(customerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authenticationRepository.getCustomerDetails(customerId)
                withContext(Dispatchers.Main) {
                    customerDetailsResponse.value = response
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

    fun editProfileResponse(request: User, image: String?) {
        var imageFileBody: MultipartBody.Part? = null
        val customer_id: RequestBody = AppUtils.getRequestBody(request.customer_id.toString())
        val first_name: RequestBody = AppUtils.getRequestBody(request.first_name)
        val last_name: RequestBody = AppUtils.getRequestBody(request.last_name)
        val email_address: RequestBody = AppUtils.getRequestBody(request.email_address)
        val date_of_birth: RequestBody = AppUtils.getRequestBody(request.date_of_birth)
        val country_id: RequestBody = AppUtils.getRequestBody(request.country_id.toString())
        val phone_extension_id: RequestBody = AppUtils.getRequestBody(request.phone_extension_id!!)
        val phone_number: RequestBody = AppUtils.getRequestBody(request.phone_number)
        val street_address: RequestBody = AppUtils.getRequestBody(request.street_address)
        val city: RequestBody = AppUtils.getRequestBody(request.city)
        val pin_code: RequestBody = AppUtils.getRequestBody(request.pin_code)
        val device_token: RequestBody = AppUtils.getRequestBody(request.device_token)
        val device_type: RequestBody = AppUtils.getRequestBody(request.device_type)

        if (!StringHelper.isEmpty(image)) {
            val file = File(image!!)
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            imageFileBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authenticationRepository.editProfile(
                    imageFileBody,
                    customer_id,
                    first_name,
                    last_name,
                    email_address,
                    date_of_birth,
                    country_id,
                    phone_extension_id,
                    phone_number,
                    street_address,
                    city,
                    pin_code,
                    device_token,
                    device_type
                )
                withContext(Dispatchers.Main) {
                    loginResponse.value = response
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

    fun changePassword(changePasswordRequest: ChangePasswordRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val changePasswordResponse =
                    authenticationRepository.changePassword(changePasswordRequest)
                withContext(Dispatchers.Main) {
                    baseResponse.value = changePasswordResponse
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

    fun forgotPasswordUserExist(email: String, guard: String) {
        val email: RequestBody = AppUtils.getRequestBody(email)
        val guard: RequestBody = AppUtils.getRequestBody(guard)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val forgotPasswordResponse =
                    authenticationRepository.forgotPasswordUserExist(email, guard)
                withContext(Dispatchers.Main) {
                    forgotPasswordUserExistResponse.value = forgotPasswordResponse
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

    fun forgotPasswordSendOtpRequest(forgotPasswordSendOtpRequest: ForgotPasswordSendOtpRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val forgotPasswordResponse =
                    authenticationRepository.forgotPasswordSendOtp(forgotPasswordSendOtpRequest)
                withContext(Dispatchers.Main) {
                    baseResponse.value = forgotPasswordResponse
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

    fun forgotPasswordVerifyOtpRequest(forgotPasswordVerifyOtpRequest: ForgotPasswordVerifyOtpRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val forgotPasswordResponse =
                    authenticationRepository.forgotPasswordVerifyOtp(forgotPasswordVerifyOtpRequest)
                withContext(Dispatchers.Main) {
                    baseResponse.value = forgotPasswordResponse
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

    fun forgotPasswordSavePasswordRequest(forgotPasswordSavePasswordRequest: ForgotPasswordSavePasswordRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val forgotPasswordResponse =
                    authenticationRepository.forgotPasswordSavePassword(
                        forgotPasswordSavePasswordRequest
                    )
                withContext(Dispatchers.Main) {
                    baseResponse.value = forgotPasswordResponse
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

    fun getDeviceIdResponse(deviceType: String, deviceToken: String, deviceModel: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    authenticationRepository.getDeviceId(deviceType, deviceToken, deviceModel)
                withContext(Dispatchers.Main) {
                    getDeviceIdResponse.value = response
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

    fun checkEmailExist(email: String, guard: String) {
        val email: RequestBody = AppUtils.getRequestBody(email)
        val guard: RequestBody = AppUtils.getRequestBody(guard)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val emailExistResponse =
                    authenticationRepository.checkEmailExist(email, guard)
                withContext(Dispatchers.Main) {
                    baseResponse.value = emailExistResponse
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