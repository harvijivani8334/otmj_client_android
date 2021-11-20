package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.LoginRequest
import com.app.otmjobs.authentication.data.model.Users
import com.app.otmjobs.authentication.ui.adapter.LoginUsersListAdapter
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.ActivityIntroductionBinding
import com.app.utilities.utils.AlertDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel


class IntroductionActivity : BaseActivity(), View.OnClickListener, SelectItemListener {
    private lateinit var binding: ActivityIntroductionBinding
    private lateinit var mContext: Context;
    private lateinit var adapter: LoginUsersListAdapter
    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_introduction)
        setStatusBarColor()
        mContext = this
        setLoginObservers()
        getDeviceIdObservers()

        binding.txtLogin.setOnClickListener(this)
        binding.txtSignUp.setOnClickListener(this)

        setLoginUserListAdapter()

        authenticationViewModel.getDeviceIdResponse(
            AppConstants.DEVICE_TYPE.toString(),
            AppUtils.getDeviceToken(),
            AppUtils.getDeviceModel()
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtLogin ->
                moveActivity(mContext, LoginActivity::class.java, false, false, null)
            R.id.txtSignUp ->
                moveActivity(mContext, SignUp1Activity::class.java, false, false, null)
        }
    }

    private fun setLoginUserListAdapter() {
        if (AppUtils.getLoginUsers(mContext) != null) {
            val users: Users = AppUtils.getLoginUsers(mContext)!!
            if (users.users.size > 0) {
                binding.recyclerView.visibility = View.VISIBLE
                val linearLayoutManager =
                    LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                binding.recyclerView.layoutManager = linearLayoutManager
                binding.recyclerView.setHasFixedSize(true)
                adapter = LoginUsersListAdapter(mContext, users.users, this)
                binding.recyclerView.adapter = adapter
            } else {
                binding.recyclerView.visibility = View.GONE
            }
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

    private fun getDeviceIdObservers() {
        authenticationViewModel.getDeviceIdResponse.observe(this) { response ->
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext, null,
                        mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                        null, false, null, 0
                    )
                } else {
                    if (response.IsSuccess) {
                        AppUtils.setDeviceId(mContext, response.device_id)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onSelectItem(position: Int, action: Int) {
        if (action == AppConstants.Action.VIEW_LOGIN_USER) {
            showProgressDialog(mContext, "")
            val loginRequest = LoginRequest()
            loginRequest.email = adapter.list[position].email_address
            loginRequest.password = adapter.list[position].password
            loginRequest.device_type = AppConstants.DEVICE_TYPE.toString()
            loginRequest.device_token = AppUtils.getDeviceToken()
            authenticationViewModel.login(loginRequest)
        } else if (action == AppConstants.Action.DELETE_LOGIN_USER) {
            val users = AppUtils.getLoginUsers(mContext)
            if (users != null) {
                users.users.removeAt(position)
                AppUtils.setLoginUsers(mContext, users)
                setLoginUserListAdapter()
            }
        }
    }
}