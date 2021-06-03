package com.app.otmjobs.dashboard.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.MyApplication
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mContext: Context;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        mContext = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setupToolbar(getString(R.string.settings), true)

        binding.routNotificationSettings.setOnClickListener(this)
        binding.routSelectLanguage.setOnClickListener(this)
        binding.routChangePassword.setOnClickListener(this)

        binding.swDarkMode.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    MyApplication().preferencePutInteger(
                        AppConstants.SharedPrefKey.THEME_MODE,
                        AppConstants.THEME_MODE.DARK
                    )
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                    MyApplication().preferencePutInteger(
                        AppConstants.SharedPrefKey.THEME_MODE,
                        AppConstants.THEME_MODE.LIGHT
                    )
                }
                moveActivity(mContext, DashBoardActivity::class.java, true, true, null)
            }
        }

        when (MyApplication().preferenceGetInteger(AppConstants.SharedPrefKey.THEME_MODE, 0)) {
            0 -> binding.swDarkMode.isChecked = false
            1 -> binding.swDarkMode.isChecked = true
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.routNotificationSettings -> {

            }
            R.id.routSelectLanguage -> {

            }
            R.id.routChangePassword -> {

            }
        }
    }
}