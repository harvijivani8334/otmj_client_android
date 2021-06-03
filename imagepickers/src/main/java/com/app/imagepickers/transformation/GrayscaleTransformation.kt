package com.app.imagepickers.transformation

import android.content.Context
import android.graphics.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import java.security.MessageDigest

class GrayscaleTransformation(private val mBitmapPool: BitmapPool) :
    Transformation<Bitmap> {
    constructor(context: Context?) : this(Glide.get(context!!).bitmapPool) {}

    override fun transform(
        context: Context,
        resource: Resource<Bitmap>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap> {
        val source = resource.get()
        val width = source.width
        val height = source.height
        val config = if (source.config != null) source.config else Bitmap.Config.ARGB_8888
        var bitmap: Bitmap = mBitmapPool[width, height, config]
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, config)
        }
        val canvas = Canvas(bitmap!!)
        val saturation = ColorMatrix()
        saturation.setSaturation(0f)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(saturation)
        canvas.drawBitmap(source, 0f, 0f, paint)
        return BitmapResource.obtain(bitmap, mBitmapPool)!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
}
