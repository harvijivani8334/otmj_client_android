package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.ChangePasswordRequest
import com.app.otmjobs.authentication.data.model.SignUpRequest
import com.app.otmjobs.authentication.data.model.User
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.ActivityChangePasswordBinding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var mContext: Context;
    private var visiblePassword: Boolean = false
    private var visibleNewPassword: Boolean = false
    private var visibleConfPassword: Boolean = false
    lateinit var changePasswordRequest: ChangePasswordRequest
    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        setStatusBarColor()
        setupToolbar(getString(R.string.lbl_change_your_password), true)
        mContext = this
        changePasswordObservers()
        changePasswordRequest = ChangePasswordRequest()
        binding.changePasswordRequest = changePasswordRequest

        binding.ivOldPasswordVisibility.setOnClickListener(this)
        binding.ivNewPasswordVisibility.setOnClickListener(this)
        binding.ivConfirmNewPasswordVisibility.setOnClickListener(this)
        binding.txtUpdate.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtUpdate -> {
                if (valid()) {
                    changePasswordRequest.guard = "api"
                    changePasswordRequest.device_type = AppConstants.DEVICE_TYPE.toString()
                    changePasswordRequest.device_token = AppUtils.getDeviceToken()
                    showProgressDialog(mContext,"")
                    authenticationViewModel.changePassword(changePasswordRequest)
                }
            }
            R.id.ivOldPasswordVisibility -> setPasswordVisibility()
            R.id.ivNewPasswordVisibility -> setNewPasswordVisibility()
            R.id.ivConfirmNewPasswordVisibility -> setConfPasswordVisibility()
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (!ValidationUtil.isEmptyEditText(binding.edtOldPassword.text.toString().trim())) {
            binding.edtOldPassword.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtOldPassword,
                mContext.getString(R.string.error_empty_old_password)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isEmptyEditText(binding.edtNewPassword.text.toString().trim())) {
            if (ValidationUtil.isValidPassword(binding.edtNewPassword.text.toString().trim())) {
                binding.edtNewPassword.error = null
            } else {
                ValidationUtil.setErrorIntoEditText(
                    binding.edtNewPassword,
                    mContext.getString(R.string.error_invalid_password)
                )
                valid = false
                return valid
            }
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtNewPassword,
                mContext.getString(R.string.error_empty_new_password)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isValidConfirmPassword(
                binding.edtConfirmNewPassword.text.toString().trim(),
                binding.edtNewPassword.text.toString().trim()
            )
        ) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtConfirmNewPassword,
                mContext.getString(R.string.error_password_not_match)
            )
            valid = false
            return valid
        }
        return valid
    }

    private fun setPasswordVisibility() {
        if (visiblePassword) {
            visiblePassword = false
            binding.edtOldPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivOldPasswordVisibility.setImageResource(R.drawable.ic_eye_visible)
            binding.edtOldPassword.setSelection(binding.edtOldPassword.text!!.length)
        } else {
            visiblePassword = true
            binding.edtOldPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.ivOldPasswordVisibility.setImageResource(R.drawable.ic_eye_invisible)
            binding.edtOldPassword.setSelection(binding.edtOldPassword.text!!.length)
        }
    }

    private fun setNewPasswordVisibility() {
        if (visibleNewPassword) {
            visibleNewPassword = false
            binding.edtNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivNewPasswordVisibility.setImageResource(R.drawable.ic_eye_visible)
            binding.edtNewPassword.setSelection(binding.edtNewPassword.text!!.length)
        } else {
            visibleNewPassword = true
            binding.edtNewPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.ivNewPasswordVisibility.setImageResource(R.drawable.ic_eye_invisible)
            binding.edtNewPassword.setSelection(binding.edtNewPassword.text!!.length)
        }
    }

    private fun setConfPasswordVisibility() {
        if (visibleConfPassword) {
            visibleConfPassword = false
            binding.edtConfirmNewPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.ivConfirmNewPasswordVisibility.setImageResource(R.drawable.ic_eye_visible)
            binding.edtConfirmNewPassword.setSelection(binding.edtConfirmNewPassword.text!!.length)
        } else {
            visibleConfPassword = true
            binding.edtConfirmNewPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.ivConfirmNewPasswordVisibility.setImageResource(R.drawable.ic_eye_invisible)
            binding.edtConfirmNewPassword.setSelection(binding.edtConfirmNewPassword.text!!.length)
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
                        user.password = changePasswordRequest.password
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