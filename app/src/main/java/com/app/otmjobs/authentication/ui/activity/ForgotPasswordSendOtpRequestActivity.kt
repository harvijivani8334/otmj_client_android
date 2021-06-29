package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.ForgotPasswordSendOtpRequest
import com.app.otmjobs.authentication.data.model.ForgotPasswordUserExistResponse
import com.app.otmjobs.authentication.data.model.SignUpRequest
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityForgotPasswordSendOtpRequestBinding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class ForgotPasswordSendOtpRequestActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordSendOtpRequestBinding
    private lateinit var mContext: Context;
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private lateinit var forgotPasswordUserExistResponse: ForgotPasswordUserExistResponse
    private lateinit var forgotPasswordSendOtpRequest: ForgotPasswordSendOtpRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_forgot_password_send_otp_request)
        setStatusBarColor()
        setupToolbar("", true)
        mContext = this
        forgotPasswordSendOtpRequest = ForgotPasswordSendOtpRequest()

        forgotPasswordObservers()

        binding.txtSubmit.setOnClickListener(this)

        getIntentData()
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            forgotPasswordUserExistResponse =
                Parcels.unwrap(intent.getParcelableExtra(AppConstants.IntentKey.FORGOT_PASSWORD_DATA))

            val email: String = forgotPasswordUserExistResponse.email
            if (!StringHelper.isEmpty(email)) {
                val emailString = email.substring(0, email.indexOf("@"))
                if (emailString.length > 2) {
                    val visibleString = email.substring(emailString.length - 2, emailString.length)
                    val invisibleLength = emailString.substring(0, emailString.length - 2).length
                    var invisibleString = ""
                    for (i in 0 until invisibleLength) {
                        invisibleString = "$invisibleString*"
                    }
                    val finalEmail = invisibleString + visibleString + email.substring(
                        email.indexOf("@"),
                        email.length
                    )
                    binding.rbEmail.text = "Email  $finalEmail"
                } else {
                    binding.rbEmail.text = "Email  $email"
                }
            }

            if (!StringHelper.isEmpty(forgotPasswordUserExistResponse.mobile)
                && forgotPasswordUserExistResponse.mobile.length > 3
            ) {
                binding.rbPhone.text = "Text  ********" + forgotPasswordUserExistResponse.mobile
                    .substring(
                        forgotPasswordUserExistResponse.mobile.length - 2,
                        forgotPasswordUserExistResponse.mobile.length
                    )
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSubmit -> {
                showProgressDialog(mContext, "")
                forgotPasswordSendOtpRequest.guard = AppConstants.guard
//                if(binding.rgSelectMethodType.checkedRadioButtonId == R.id.rbEmail){
//                    forgotPasswordSendOtpRequest.option = "email"
//                    forgotPasswordSendOtpRequest.email = forgotPasswordUserExistResponse.email
//                }else{
                forgotPasswordSendOtpRequest.option = "phone"
                forgotPasswordSendOtpRequest.mobile = forgotPasswordUserExistResponse.mobile
                forgotPasswordSendOtpRequest.mobileWithExtension =
                    forgotPasswordUserExistResponse.mobileWithExtension
//                }
                authenticationViewModel.forgotPasswordSendOtpRequest(forgotPasswordSendOtpRequest)
            }
        }
    }

    private fun forgotPasswordObservers() {
        authenticationViewModel.baseResponse.observe(this) { response ->
            hideProgressDialog()
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext, null,
                        mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                        null, false, null, 0
                    )
                } else {
                    if (response.IsSuccess) {
                        val bundle = Bundle()
                        bundle.putString(
                            AppConstants.IntentKey.EMAIL,
                            forgotPasswordUserExistResponse.email
                        )
                        moveActivity(
                            mContext,
                            ForgotPasswordVerifyOtpActivity::class.java,
                            false,
                            false,
                            bundle
                        )
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }
}