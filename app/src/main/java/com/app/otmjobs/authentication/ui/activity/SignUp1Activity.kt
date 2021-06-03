package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.SignUpRequest
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.ActivitySignup1Binding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class SignUp1Activity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignup1Binding
    private lateinit var mContext: Context;
    lateinit var signUpRequest: SignUpRequest
    private var visiblePassword: Boolean = false
    private var visibleConfPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_1)
        setStatusBarColor()
        setupToolbar("", true)
        mContext = this
        signUpRequest = SignUpRequest()
//        signUpRequest.first_name = "RK"
//        signUpRequest.last_name = "RK"
//        signUpRequest.email_address = "rjk@gmail.com"
//        signUpRequest.password = "Rk@123"
//        signUpRequest.confirm_password = "Rk@123"

        binding.signUpRequest = signUpRequest

        binding.ivPasswordVisibility.setOnClickListener(this)
        binding.ivConfirmPasswordVisibility.setOnClickListener(this)
        binding.txtNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtNext -> {
                if (valid()) {
                    val bundle = Bundle()
                    bundle.putParcelable(
                        AppConstants.IntentKey.SIGN_UP_REQUEST_DATA,
                        Parcels.wrap(signUpRequest)
                    )
                    moveActivity(mContext, SignUp2Activity::class.java, false, false, bundle)
                }
            }
            R.id.ivPasswordVisibility -> setPasswordVisibility()
            R.id.ivConfirmPasswordVisibility -> setConfPasswordVisibility()
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (!ValidationUtil.isEmptyEditText(signUpRequest.first_name)) {
            binding.edtFirstName.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtFirstName,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isEmptyEditText(signUpRequest.last_name)) {
            binding.edtLastName.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtLastName,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isEmptyEditText(signUpRequest.email_address)) {
            if (ValidationUtil.isValidEmail(binding.edtEmailId.text.toString())) {
                binding.edtEmailId.error = null
            } else {
                ValidationUtil.setErrorIntoEditText(
                    binding.edtEmailId,
                    mContext.getString(R.string.error_valid_email)
                )
                valid = false
                return valid
            }
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtEmailId,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isEmptyEditText(signUpRequest.password)) {
            if (ValidationUtil.isValidPassword(signUpRequest.password)) {
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
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isEmptyEditText(signUpRequest.confirm_password)) {
            binding.edtConfirmPassword.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtConfirmPassword,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (valid) {
            if (!ValidationUtil.isValidConfirmPassword(
                    signUpRequest.confirm_password,
                    signUpRequest.password
                )
            ) {
                ValidationUtil.setErrorIntoEditText(
                    binding.edtConfirmPassword,
                    mContext.getString(R.string.error_password_not_match)
                )
                valid = false
            }
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

    private fun setConfPasswordVisibility() {
        if (visibleConfPassword) {
            visibleConfPassword = false
            binding.edtConfirmPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.ivConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye_visible)
            binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.text!!.length)
        } else {
            visibleConfPassword = true
            binding.edtConfirmPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.ivConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye_invisible)
            binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.text!!.length)
        }
    }
}