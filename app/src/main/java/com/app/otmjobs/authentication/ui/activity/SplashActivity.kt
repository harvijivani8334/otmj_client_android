package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.MyApplication
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.ActivitySplashBinding
import com.app.utilities.utils.StringHelper

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mContext: Context;
    private val splashTimeOut: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        setStatusBarColor()
        mContext = this

        Handler(Looper.getMainLooper()).postDelayed({
            val userInfo: String =
                MyApplication().preferenceGetString(AppConstants.SharedPrefKey.USER_INFO, "")
            if (StringHelper.isEmpty(userInfo) || userInfo.equals("null", ignoreCase = true)) {
                moveActivity(mContext, IntroductionActivity::class.java, true, true, null)
            } else {
                moveActivity(mContext, DashBoardActivity::class.java, true, true, null)
            }
        }, splashTimeOut)

    }

}