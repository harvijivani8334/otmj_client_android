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
import com.app.otmjobs.databinding.ActivityForgotPasswordBinding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordUserExistActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var mContext: Context;
    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        setStatusBarColor()
        setupToolbar("", true)
        mContext = this
        forgotPasswordUserExistObservers()

        binding.txtSend.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSend -> {
                if (valid()) {
                    showProgressDialog(mContext, "")
                    authenticationViewModel.forgotPasswordUserExist(
                        binding.edtEmail.text.toString().trim(), AppConstants.guard
                    )
                }
            }
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (!ValidationUtil.isEmptyEditText(binding.edtEmail.text.toString().trim())) {
            binding.edtEmail.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtEmail,
                mContext.getString(R.string.error_email)
            )
            valid = false
            return valid
        }
        return valid
    }

    private fun forgotPasswordUserExistObservers() {
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