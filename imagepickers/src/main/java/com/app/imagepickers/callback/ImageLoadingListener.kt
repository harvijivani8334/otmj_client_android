package com.app.imagepickers.callback

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

interface ImageLoadingListener {
    fun onLoaded(bitmap: Bitmap?, glideDrawable: Drawable?)

    fun onFailed(exception: Exception?)
}