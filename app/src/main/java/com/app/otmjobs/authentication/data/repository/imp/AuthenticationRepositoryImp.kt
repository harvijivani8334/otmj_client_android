package com.app.otmjobs.authentication.data.repository.imp

import com.app.otmjobs.MyApplication
import com.app.otmjobs.authentication.data.model.*
import com.app.otmjobs.authentication.data.remote.AuthenticationInterface
import com.app.otmjobs.authentication.data.repository.AuthenticationRepository
import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.common.utils.AppUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthenticationRepositoryImp(
    private val authenticationInterface: AuthenticationInterface
) : AuthenticationRepository {
    override suspend fun login(loginRequest: LoginRequest): UserResponse {
        val userLogin = authenticationInterface.login(loginRequest)
        if (userLogin.IsSuccess) {
            userLogin.info.password = loginRequest.password
            AppUtils.setUserPreference(MyApplication.context, userLogin.info)
        }
        return userLogin;
    }

    override suspend fun register(signUpRequest: SignUpRequest): UserResponse {
        val userLogin = authenticationInterface.register(signUpRequest)
        if (userLogin.IsSuccess) {
            userLogin.info.password = signUpRequest.password
            AppUtils.setUserPreference(MyApplication.context, userLogin.info)
        }
        return userLogin;
    }

    override suspend fun getPhoneExtension(countryCode: String): PhoneExtensionResponse {
        return authenticationInterface.getPhoneExtension(countryCode)
    }

    override suspend fun getCountries(): CountryResponse {
        return authenticationInterface.getCountries()
    }

    override suspend fun getCustomerDetails(customerId: Int): UserResponse {
        return authenticationInterface.getCustomerDetails(customerId)
    }

    override suspend fun editProfile(
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
    ): UserResponse {
        return authenticationInterface.editProfile(
            image,
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
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): BaseResponse {
        return authenticationInterface.changePassword(changePasswordRequest)
    }

    override suspend fun forgotPasswordUserExist(
        email: RequestBody,
        guard: RequestBody
    ): ForgotPasswordUserExistResponse {
        return authenticationInterface.forgotPasswordUserExist(email, guard)
    }

    override suspend fun forgotPasswordSendOtp(forgotPasswordSendOtpRequest: ForgotPasswordSendOtpRequest): BaseResponse {
        return authenticationInterface.forgotPasswordSendOtp(forgotPasswordSendOtpRequest)
    }

    override suspend fun forgotPasswordVerifyOtp(forgotPasswordSendOtpRequest: ForgotPasswordVerifyOtpRequest): BaseResponse {
        return authenticationInterface.forgotPasswordVerifyOtp(forgotPasswordSendOtpRequest)
    }

    override suspend fun forgotPasswordSavePassword(forgotPasswordSavePasswordRequest: ForgotPasswordSavePasswordRequest): BaseResponse {
        return authenticationInterface.forgotPasswordSavePassword(forgotPasswordSavePasswordRequest)
    }

    override suspend fun getDeviceId(
        device_type: String,
        device_token: String,
        device_model: String
    ): GetDeviceIdResponse {
        return authenticationInterface.getDeviceId(device_type, device_token, device_model)
    }
}
