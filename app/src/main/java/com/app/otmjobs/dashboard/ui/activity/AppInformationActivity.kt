package com.app.otmjobs.dashboard.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityAppInformationBinding

class AppInformationActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAppInformationBinding
    private lateinit var mContext: Context;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        mContext = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_information)
        setupToolbar(getString(R.string.app_information), true)

        binding.routHelpSupport.setOnClickListener(this)
        binding.routPrivacyPolicy.setOnClickListener(this)
        binding.routTermsConditions.setOnClickListener(this)
        binding.txtWebsite.setOnClickListener(this)

        binding.txtVersion.text = AppUtils.getApplicationVersion(mContext)
    }

    override fun onClick(v: View) {
        val bundle = Bundle()
        when (v.id) {
            R.id.routPrivacyPolicy -> {

            }
            R.id.routTermsConditions -> {

            }
            R.id.txtWebsite -> {
                AppUtils.opeUrlInBrowser(mContext, getString(R.string.website_url))
            }
        }
    }
}