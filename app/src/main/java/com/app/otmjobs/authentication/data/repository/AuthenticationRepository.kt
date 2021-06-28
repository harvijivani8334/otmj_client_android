package com.app.otmjobs.authentication.data.repository

import com.app.otmjobs.authentication.data.model.*
import com.app.otmjobs.common.data.model.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthenticationRepository {
    suspend fun login(loginRequest: LoginRequest): UserResponse
    suspend fun register(signUpRequest: SignUpRequest): UserResponse
    suspend fun getPhoneExtension(countryCode: String): PhoneExtensionResponse
    suspend fun getCountries(): CountryResponse
    suspend fun editProfile(
        image: MultipartBody.Part?,
        customer_id: RequestBody,
        first_name: RequestBody,
        last_name: RequestBody,
        email_address: RequestBody,
        date_of_birth: RequestBody,
        country_id: RequestBody,
        phone_extension_id: RequestBody,
        phone_number: RequestBody,
        street_address: RequestBody,
        city: RequestBody,
        pin_code: RequestBody,
        device_token: RequestBody,
        device_type: RequestBody
    ): UserResponse

    suspend fun getCustomerDetails(customerId: Int): UserResponse
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): BaseResponse
    suspend fun forgotPasswordUserExist(
        email: RequestBody,
        guard: RequestBody
    ): ForgotPasswordUserExistResponse

    suspend fun forgotPasswordSendOtp(forgotPasswordSendOtpRequest: ForgotPasswordSendOtpRequest): BaseResponse
    suspend fun forgotPasswordVerifyOtp(forgotPasswordSendOtpRequest: ForgotPasswordVerifyOtpRequest): BaseResponse
    suspend fun forgotPasswordSavePassword(forgotPasswordSavePasswordRequest: ForgotPasswordSavePasswordRequest): BaseResponse
}