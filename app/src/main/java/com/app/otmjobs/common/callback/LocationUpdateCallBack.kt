package com.app.otmjobs.common.callback

import android.location.Location

interface LocationUpdateCallBack {
    fun locationUpdate(location: Location)
}