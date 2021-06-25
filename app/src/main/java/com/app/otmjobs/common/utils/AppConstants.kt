package com.app.otmjobs.common.utils

import androidx.work.Operation
import com.app.utilities.utils.DateFormatsConstants

object AppConstants {
    const val UNAUTHORIZED = 401
    const val DEVICE_TYPE = 1
    const val CURRENCY = "₤"
    const val MAX_IMAGE_WIDTH = 1280
    const val MAX_IMAGE_HEIGHT = 1280
    const val IMAGE_QUALITY = 80
    const val defaultDateFormat = DateFormatsConstants.DD_MMM_YYYY_SPACE
    const val apiDateFormat = DateFormatsConstants.DD_MM_YYYY_DASH
    const val defaultCountryCode = "GB"

    object IntentKey {
        const val POST_JOB_TYPE = "POST_JOB_TYPE"
        const val SIGN_UP_REQUEST_DATA = "SIGN_UP_REQUEST_DATA"
        const val CATEGORY_DATA = "TRADES_DATA"
        const val CATEGORY_ID = "CATEGORY_ID"
        const val CATEGORY_TITLE = "CATEGORY_TITLE"
        const val POST_JOB_DATA = "POST_JOB_DATA"
        const val JOB_ID = "JOB_ID"
        const val IMAGE_URI = "image_uri"
        const val CROP_RATIO_X = "crop_ratio_X"
        const val CROP_RATIO_Y = "crop_ratio_Y"
        const val FILE_EXTENSION = "file_extension"
        const val POST_JOB_PHOTOS = "POST_JOB_PHOTOS"
        const val USER_ID = "USER_ID"
        const val JOB_APPLICATION_ID = "JOB_APPLICATION_ID"
        const val RC_LOCATION_PERM = 1
        const val LOCATION_SETTING_STATUS = 2
        const val SELECT_CATEGORY = 3
        const val SELECT_SUB_CATEGORY = 4
        const val ADD_JOB = 5
        const val REQUEST_CROP_IMAGE = 6
        const val EXTERNAL_STORAGE_PERMISSION = 7
    }

    object SharedPrefKey {
        const val USER_INFO = "USER_INFO"
        const val USERS = "USERS"
        const val APP_URL = "APP_URL"
        const val THEME_MODE = "THEME_MODE"
    }

    object THEME_MODE {
        const val LIGHT = 0
        const val DARK = 1
    }

    object DialogIdentifier {
        const val LOGOUT = 1
        const val SELECT_COUNTRY_FLAG = 2
        const val SELECT_COUNTRY2_FLAG = 3
        const val SELECT_POST_CODE = 4
        const val SELECT_POST_CODE2 = 5
        const val DELETE_JOB = 6
        const val START_DATE = "START_DATE"
        const val END_DATE = "END_DATE"
        const val DOB_PICKER = "DOB_PICKER"
    }

    object LocationMode {
        const val LOCATION_MODE_OFF = 0
        const val LOCATION_MODE_SENSORS_ONLY = 1
        const val LOCATION_MODE_BATTERY_SAVING = 2
        const val LOCATION_MODE_HIGH_ACCURACY = 3
    }

    object Action {
        const val SELECT_CATEGORY = 1
        const val VIEW_JOB = 1
        const val EDIT_JOB = 2
        const val DELETE_JOB = 3
        const val ADD_PHOTO = 4
        const val DELETE_PHOTO = 5
        const val SELECT_JOB_START_FROM = 6
        const val DELETE_LOGIN_USER = 7
        const val VIEW_LOGIN_USER = 8
        const val MARK_AS_COMPLETED_JOB = 9
        const val MARK_AS_PAUSED_JOB = 10
        const val JOB_HISTORY = 11
        const val VIEW_USER = 12
    }

    object FileExtension {
        const val JPG = ".jpg"
        const val PNG = ".png"
        const val PDF = ".pdf"
        const val MP3 = ".mp3"
        const val M4A = ".m4a"
    }

    object Type {
        const val CAMERA = "camera"
        const val PDF = "pdf"
        const val SELECT_FROM_CAMERA = 1
        const val SELECT_PHOTOS = 2
        const val JOB_TYPE_REGULAR = "1"
        const val JOB_TYPE_EMERGENCY = "2"
        const val JOB_TYPE_POPULAR = "3"
    }

    object Directory {
        const val DEFAULT = "otmjobs"
        const val IMAGES = "otmjobs/images"
    }

    object Status {
        const val SUCCESS = 1
        const val ERROR = 2
        const val LOADING = 3
    }

    object JobStatus {
        const val Live = 1
        const val Paused = 2
        const val Completed = 3
        const val Reposted = 4
        const val Deleted = 5
    }
}

