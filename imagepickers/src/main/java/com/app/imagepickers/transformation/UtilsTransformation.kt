package com.app.imagepickers.transformation

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build

object UtilsTransformation {
    fun getMaskDrawable(context: Context, maskId: Int): Drawable {
        val drawable: Drawable?
        drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(maskId)
        } else {
            context.resources.getDrawable(maskId)
        }
        requireNotNull(drawable) { "maskId is invalid" }
        return drawable
    }
}
