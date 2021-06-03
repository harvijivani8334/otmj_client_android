package com.app.imagepickers.transformation

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import java.security.MessageDigest

class MaskTransformation(context: Context, maskId: Int) :
    Transformation<Bitmap?> {
    private val mContext: Context
    private val mBitmapPool: BitmapPool
    private val mMaskId: Int

    companion object {
        private val mMaskingPaint = Paint()

        init {
            mMaskingPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
    }

    override fun transform(
        context: Context,
        resource: Resource<Bitmap?>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap?> {
        val source = resource.get()
        val width = source.width
        val height = source.height
        var result: Bitmap? = mBitmapPool[width, height, Bitmap.Config.ARGB_8888]
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        val mask: Drawable = UtilsTransformation.getMaskDrawable(mContext, mMaskId)
        val canvas = Canvas(result!!)
        mask.setBounds(0, 0, width, height)
        mask.draw(canvas)
        canvas.drawBitmap(source, 0f, 0f, mMaskingPaint)
        return BitmapResource.obtain(result, mBitmapPool)!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}

    /**
     * @param maskId If you change the mask file, please also rename the mask file, or Glide will get
     * the cache with the old mask. Because getId() return the same values if using the
     * same make file name. If you have a good idea please tell us, thanks.
     */
    init {
        mBitmapPool = Glide.get(context).bitmapPool
        mContext = context
        mMaskId = maskId
    }
}