package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.app.utilities.utils.StringHelper
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.LoginRequest
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    lateinit var binding: ActivityMainBinding
    lateinit var loginRequest: LoginRequest;
    lateinit var context: Context;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        context = this
        setStatusBarColor()

        setLoginObservers()

        binding.btnLogin.setOnClickListener {
            if (valid()) {
                loginRequest = LoginRequest(
                    binding.email.text.toString().trim(),
                    binding.password.text.toString().trim()
                );
                Toast.makeText(context, "Login Call", Toast.LENGTH_SHORT).show()
                authenticationViewModel.login(loginRequest)
            } else {
                Toast.makeText(context, "Enter email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoginObservers() {
        authenticationViewModel.loginResponse.observe(this) { userResponse ->
            if (userResponse.IsSuccess) {

            }
        }
    }

    private fun valid(): Boolean {
        var isValid = true
        if (StringHelper.isEmpty(binding.email.text.toString().trim())) {
            isValid = false;
        }
        if (StringHelper.isEmpty(binding.password.text.toString().trim())) {
            isValid = false;
        }
        return isValid
    }
}