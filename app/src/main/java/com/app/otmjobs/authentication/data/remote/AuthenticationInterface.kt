package com.app.otmjobs.authentication.data.remote

import com.app.otmjobs.authentication.data.model.*
import com.app.otmjobs.common.data.model.BaseResponse
import com.app.otmjobs.managejob.data.model.WorkDetailsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AuthenticationInterface {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): UserResponse

    @POST("register")
    suspend fun register(@Body signUpRequest: SignUpRequest): UserResponse

    @GET("get-phone-extension")
    suspend fun getPhoneExtension(@Query("country_name") country_name: String): PhoneExtensionResponse

    @GET("get-phone-extension")
    suspend fun getCountries(): CountryResponse

    @GET("get-customer")
    suspend fun getCustomerDetails(@Query("customer_id") customer_id: Int): UserResponse

//    @Multipart
//    @POST("edit-profile")
//    suspend fun editProfile(
//        @Body user: User,
//        @Part image: MultipartBody.Part?
//    ): UserResponse

    @Multipart
    @POST("edit-profile")
    suspend fun editProfile(
        @Part image: MultipartBody.Part?,
        @Part("customer_id") customer_id: RequestBody,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("email_address") email_address: RequestBody,
        @Part("date_of_birth") date_of_birth: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("phone_extension_id") phone_extension_id: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part("street_address") street_address: RequestBody,
        @Part("city") city: RequestBody,
        @Part("pin_code") pin_code: RequestBody,
        @Part("device_token") device_token: RequestBody,
        @Part("device_type") device_type: RequestBody,
    ): UserResponse

    @POST("change-password")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): BaseResponse

    @Multipart
    @POST("forgot-password/user-exist")
    suspend fun forgotPasswordUserExist(
        @Part("email") email: RequestBody, @Part("guard") guard: RequestBody
    ): ForgotPasswordUserExistResponse

    @POST("forgot-password/send-otp")
    suspend fun forgotPasswordSendOtp(@Body forgotPasswordSendOtpRequest: ForgotPasswordSendOtpRequest): BaseResponse

    @POST("forgot-password/verify-otp")
    suspend fun forgotPasswordVerifyOtp(@Body forgotPasswordVerifyOtpRequest: ForgotPasswordVerifyOtpRequest): BaseResponse

    @POST("forgot-password/save-password")
    suspend fun forgotPasswordSavePassword(@Body forgotPasswordSavePasswordRequest: ForgotPasswordSavePasswordRequest): BaseResponse
}