package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.LoginRequest
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.ActivityLoginBinding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mContext: Context
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    lateinit var loginRequest: LoginRequest
    private var visiblePassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setStatusBarColor()
        setupToolbar("", true)
        mContext = this
        setLoginObservers()
        loginRequest = LoginRequest()
        binding.loginRequest = loginRequest

        binding.txtLogin.setOnClickListener(this)
        binding.txtSignUp.setOnClickListener(this)
        binding.ivPasswordVisibility.setOnClickListener(this)
        binding.txtForgotPassword.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtLogin -> {
                if (valid()) {
                    showProgressDialog(mContext, "")
                    loginRequest.device_type = AppConstants.DEVICE_TYPE.toString()
                    loginRequest.device_token = AppUtils.getDeviceToken()
                    authenticationViewModel.login(loginRequest)
                }
            }
            R.id.txtSignUp ->
                moveActivity(mContext, SignUp1Activity::class.java, false, false, null)
            R.id.ivPasswordVisibility ->
                setPasswordVisibility()
            R.id.txtForgotPassword ->
                moveActivity(
                    mContext,
                    ForgotPasswordUserExistActivity::class.java,
                    false,
                    false,
                    null
                )
        }
    }

    private fun setLoginObservers() {
        authenticationViewModel.loginResponse.observe(this) { response ->
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
                        moveActivity(mContext, DashBoardActivity::class.java, true, true, null)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (!ValidationUtil.isEmptyEditText(loginRequest.password)) {
            binding.edtPassword.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtPassword,
                mContext.getString(R.string.error_pwd_mandatory)
            )
            valid = false
        }
        if (!ValidationUtil.isEmptyEditText(loginRequest.email)) {
            binding.edtEmail.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtEmail,
                mContext.getString(R.string.error_email)
            )
            valid = false
        }
        return valid
    }

    private fun setPasswordVisibility() {
        if (visiblePassword) {
            visiblePassword = false
            binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivPasswordVisibility.setImageResource(R.drawable.ic_eye_visible)
            binding.edtPassword.setSelection(binding.edtPassword.text!!.length)
        } else {
            visiblePassword = true
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivPasswordVisibility.setImageResource(R.drawable.ic_eye_invisible)
            binding.edtPassword.setSelection(binding.edtPassword.text!!.length)
        }
    }
}