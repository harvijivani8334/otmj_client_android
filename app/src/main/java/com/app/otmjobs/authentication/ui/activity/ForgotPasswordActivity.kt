package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.ForgotPasswordSavePasswordRequest
import com.app.otmjobs.authentication.data.model.User
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityForgotPasswordBinding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var mContext: Context;
    private var visibleNewPassword: Boolean = false
    private var visibleConfPassword: Boolean = false
    private lateinit var forgotPasswordSavePasswordRequest: ForgotPasswordSavePasswordRequest
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        setStatusBarColor()
        setupToolbar("", true)
        mContext = this
        changePasswordObservers()
        forgotPasswordSavePasswordRequest = ForgotPasswordSavePasswordRequest()
        binding.forgotPasswordRequest = forgotPasswordSavePasswordRequest

        binding.txtSubmit.setOnClickListener(this)
        binding.ivNewPasswordVisibility.setOnClickListener(this)
        binding.ivConfirmNewPasswordVisibility.setOnClickListener(this)

        getIntentData()
    }
    
    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            email = intent.getStringExtra(AppConstants.IntentKey.EMAIL)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSubmit -> {
                if (valid()) {
                    forgotPasswordSavePasswordRequest.guard = AppConstants.guard
                    forgotPasswordSavePasswordRequest.email = email!!
                    showProgressDialog(mContext, "")
                    authenticationViewModel.forgotPasswordSavePasswordRequest(
                        forgotPasswordSavePasswordRequest
                    )
                }
            }
            R.id.ivNewPasswordVisibility -> setNewPasswordVisibility()
            R.id.ivConfirmNewPasswordVisibility -> setConfPasswordVisibility()
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (!ValidationUtil.isEmptyEditText(binding.edtPassword.text.toString().trim())) {
            if (ValidationUtil.isValidPassword(binding.edtPassword.text.toString().trim())) {
                binding.edtPassword.error = null
            } else {
                ValidationUtil.setErrorIntoEditText(
                    binding.edtPassword,
                    mContext.getString(R.string.error_invalid_password)
                )
                valid = false
                return valid
            }
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtPassword,
                mContext.getString(R.string.error_empty_new_password)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isValidConfirmPassword(
                binding.edtPassword.text.toString().trim(),
                binding.edtConfirmPassword.text.toString().trim()
            )
        ) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtConfirmPassword,
                mContext.getString(R.string.error_password_not_match)
            )
            valid = false
            return valid
        }
        return valid
    }

    private fun setNewPasswordVisibility() {
        if (visibleNewPassword) {
            visibleNewPassword = false
            binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivNewPasswordVisibility.setImageResource(R.drawable.ic_eye_visible)
            binding.edtPassword.setSelection(binding.edtPassword.text!!.length)
        } else {
            visibleNewPassword = true
            binding.edtPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.ivNewPasswordVisibility.setImageResource(R.drawable.ic_eye_invisible)
            binding.edtPassword.setSelection(binding.edtPassword.text!!.length)
        }
    }

    private fun setConfPasswordVisibility() {
        if (visibleConfPassword) {
            visibleConfPassword = false
            binding.edtConfirmPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.ivConfirmNewPasswordVisibility.setImageResource(R.drawable.ic_eye_visible)
            binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.text!!.length)
        } else {
            visibleConfPassword = true
            binding.edtConfirmPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.ivConfirmNewPasswordVisibility.setImageResource(R.drawable.ic_eye_invisible)
            binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.text!!.length)
        }
    }

    private fun changePasswordObservers() {
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
                        val user: User = AppUtils.getUserPreference(mContext)!!
                        user.password = forgotPasswordSavePasswordRequest.password
                        AppUtils.setUserPreference(mContext, user)
                        moveActivity(mContext, IntroductionActivity::class.java, true, true, null)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }
}