package com.app.otmjobs.common.utils

import com.app.otmjobs.MyApplication

object VariantConfig {
    val serverBaseUrl: String
        get() {
            return MyApplication().preferenceGetString(
                AppConstants.SharedPrefKey.APP_URL,
                "http://dev.otmjobs.com/api/"
            )
        }
}