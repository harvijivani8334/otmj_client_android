package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.ForgotPasswordUserExistResponse
import com.app.otmjobs.authentication.data.model.ForgotPasswordVerifyOtpRequest
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.callback.EnterOtpListener
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityForgotPasswordVerifyOtpBinding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import com.app.utilities.utils.ToastHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class ForgotPasswordVerifyOtpActivity : BaseActivity(), View.OnClickListener, View.OnKeyListener,
    EnterOtpListener {
    public lateinit var binding: ActivityForgotPasswordVerifyOtpBinding
    private lateinit var mContext: Context;
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private lateinit var forgotPasswordVerifyOtpRequest: ForgotPasswordVerifyOtpRequest
    private var code: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_forgot_password_verify_otp)
        setStatusBarColor()
        setupToolbar("", true)
        mContext = this
        forgotPasswordVerifyOtpRequest = ForgotPasswordVerifyOtpRequest()
        verifyOtpObservers()

        binding.txtSubmit.setOnClickListener(this)
        binding.txtResendOtp.setOnClickListener(this)

        binding.edtVerifyCode1.addTextChangedListener(
            GenericTextWatcher(
                binding.edtVerifyCode1,
                this
            )
        )
        binding.edtVerifyCode2.addTextChangedListener(
            GenericTextWatcher(
                binding.edtVerifyCode2,
                this
            )
        )
        binding.edtVerifyCode3.addTextChangedListener(
            GenericTextWatcher(
                binding.edtVerifyCode3,
                this
            )
        )
        binding.edtVerifyCode4.addTextChangedListener(
            GenericTextWatcher(
                binding.edtVerifyCode4,
                this
            )
        )

        binding.edtVerifyCode1.setOnKeyListener(this)
        binding.edtVerifyCode2.setOnKeyListener(this)
        binding.edtVerifyCode3.setOnKeyListener(this)
        binding.edtVerifyCode4.setOnKeyListener(this)

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
                if (validateCode()) {
//                    forgotPasswordVerifyOtpRequest.email = email!!
//                    forgotPasswordVerifyOtpRequest.guard = AppConstants.guard
//                    forgotPasswordVerifyOtpRequest.otp = code!!
//
//                    showProgressDialog(mContext, "")
//                    authenticationViewModel.forgotPasswordVerifyOtpRequest(
//                        forgotPasswordVerifyOtpRequest
//                    )
                    val bundle = Bundle()
                    bundle.putString(
                        AppConstants.IntentKey.EMAIL,
                        email
                    )
                    moveActivity(
                        mContext,
                        ForgotPasswordActivity::class.java,
                        false,
                        false,
                        bundle
                    )
                }
            }
            R.id.txtResendOtp -> {

            }
        }
    }

    private fun verifyOtpObservers() {
        authenticationViewModel.baseResponse.observe(this) { response ->
            hideProgressDialog()
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext,
                        null,
                        mContext.getString(R.string.error_unknown),
                        mContext.getString(R.string.ok),
                        null,
                        false,
                        null,
                        0
                    )
                } else {
                    if (response.IsSuccess) {

                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    class GenericTextWatcher(private val view: View, private val listener: EnterOtpListener) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.edtVerifyCode_1 -> if (!StringHelper.isEmpty(text)) {
                    listener.requestFocus(1)
                }
                R.id.edtVerifyCode_2 -> if (StringHelper.isEmpty(text)) {
                    listener.requestFocus(1)
                } else {
                    listener.requestFocus(3)
                }
                R.id.edtVerifyCode_3 -> if (StringHelper.isEmpty(text)) {
                    listener.requestFocus(2)
                } else {
                    listener.requestFocus(4)
                }
                R.id.edtVerifyCode_4 -> if (StringHelper.isEmpty(text)) {
                    listener.requestFocus(3)
                } else {
                    listener.requestFocus(4)
                }
            }
        }
    }

    override fun requestFocus(which: Int) {
        when (which) {
            1 -> binding.edtVerifyCode1.requestFocus()
            2 -> binding.edtVerifyCode2.requestFocus()
            3 -> binding.edtVerifyCode3.requestFocus()
            4 -> binding.edtVerifyCode4.requestFocus()
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action != KeyEvent.ACTION_DOWN) return false

        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (v!!.id == R.id.edtVerifyCode_2) {
                binding.edtVerifyCode1.requestFocus()
            } else if (v.id == R.id.edtVerifyCode_3) {
                binding.edtVerifyCode2.requestFocus()
            } else if (v.id == R.id.edtVerifyCode_4) {
                binding.edtVerifyCode3.requestFocus()
            }
        } else if (keyCode == KeyEvent.KEYCODE_0 || keyCode == KeyEvent.KEYCODE_1 || keyCode == KeyEvent.KEYCODE_2 || keyCode == KeyEvent.KEYCODE_3 || keyCode == KeyEvent.KEYCODE_4 || keyCode == KeyEvent.KEYCODE_5 || keyCode == KeyEvent.KEYCODE_6 || keyCode == KeyEvent.KEYCODE_7 || keyCode == KeyEvent.KEYCODE_8 || keyCode == KeyEvent.KEYCODE_9) {
            if (v!!.id == R.id.edtVerifyCode_1) {
                if (binding.edtVerifyCode1.text.isNotEmpty()) {
                    binding.edtVerifyCode1.setText(getNumberKey(keyCode).toString())
                    binding.edtVerifyCode2.requestFocus()
                    binding.edtVerifyCode1.setSelection(binding.edtVerifyCode1.text.length)
                }
            } else if (v.id == R.id.edtVerifyCode_2) {
                if (binding.edtVerifyCode2.text.isNotEmpty()) {
                    binding.edtVerifyCode2.setText(getNumberKey(keyCode).toString())
                    binding.edtVerifyCode3.requestFocus()
                    binding.edtVerifyCode2.setSelection(binding.edtVerifyCode2.text.length)
                }
            } else if (v.id == R.id.edtVerifyCode_3) {
                if (binding.edtVerifyCode3.text.isNotEmpty()) {
                    binding.edtVerifyCode3.setText(getNumberKey(keyCode).toString())
                    binding.edtVerifyCode4.requestFocus()
                    binding.edtVerifyCode3.setSelection(binding.edtVerifyCode3.text.length)
                }
            } else if (v.id == R.id.edtVerifyCode_4) {
                if (binding.edtVerifyCode4.text.isNotEmpty()) {
                    binding.edtVerifyCode4.setText(getNumberKey(keyCode).toString())
                    binding.edtVerifyCode4.setSelection(binding.edtVerifyCode4.text.length)
                }
            }
        }
        return false
    }

    private fun getNumberKey(keycode: Int): Int {
        var key = 0
        when (keycode) {
            KeyEvent.KEYCODE_0 -> key = 0
            KeyEvent.KEYCODE_1 -> key = 1
            KeyEvent.KEYCODE_2 -> key = 2
            KeyEvent.KEYCODE_3 -> key = 3
            KeyEvent.KEYCODE_4 -> key = 4
            KeyEvent.KEYCODE_5 -> key = 5
            KeyEvent.KEYCODE_6 -> key = 6
            KeyEvent.KEYCODE_7 -> key = 7
            KeyEvent.KEYCODE_8 -> key = 8
            KeyEvent.KEYCODE_9 -> key = 9
        }
        return key
    }

    private fun validateCode(): Boolean {
        if (StringHelper.isEmpty(binding.edtVerifyCode1.text.toString().trim())) {
            ToastHelper.error(
                mContext,
                getString(R.string.error_verify_code),
                Toast.LENGTH_SHORT,
                false
            )
            return false
        } else if (StringHelper.isEmpty(binding.edtVerifyCode2.text.toString().trim())) {
            ToastHelper.error(
                mContext,
                getString(R.string.error_verify_code),
                Toast.LENGTH_SHORT,
                false
            )
            return false
        } else if (StringHelper.isEmpty(binding.edtVerifyCode3.text.toString().trim())) {
            ToastHelper.error(
                mContext,
                getString(R.string.error_verify_code),
                Toast.LENGTH_SHORT,
                false
            )
            return false
        } else if (StringHelper.isEmpty(binding.edtVerifyCode4.text.toString().trim())) {
            ToastHelper.error(
                mContext,
                getString(R.string.error_verify_code),
                Toast.LENGTH_SHORT,
                false
            )
            return false
        } else {
            code = (binding.edtVerifyCode1.text.toString()
                    + binding.edtVerifyCode2.text.toString()
                    + binding.edtVerifyCode3.text.toString()
                    + binding.edtVerifyCode4.text.toString())
            //            setOtp(code);
        }
        return true
    }
}