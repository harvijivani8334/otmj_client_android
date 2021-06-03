package com.app.imagepickers.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.app.imagepickers.transformation.BlurTransformation
import com.app.imagepickers.transformation.ColorFilterTransformation
import com.app.imagepickers.transformation.GrayscaleTransformation
import com.app.imagepickers.transformation.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.app.imagepickers.GlideApp
import com.app.imagepickers.callback.ImageLoadingListener
import java.io.File

object GlideUtil {
    private lateinit var options: RequestOptions

    /**
     * TODO : If you don't want to scale image then scaleType = 0;
     *
     * @param url
     * @param imageView
     * @param placeHolder
     * @param errorDrawable
     * @param scaleType                    ( Constant.ImageScaleType.CENTER_CROP , Constant.ImageScaleType.FIT_CENTER )
     * @param imageLoadingListener<String></String>, Bitmap>( If you don't want to get success and error of image loading then pass null )
     */
    fun loadImage(
        url: String?, imageView: ImageView, placeHolder: Drawable?,
        errorDrawable: Drawable?, scaleType: Int, imageLoadingListener: ImageLoadingListener?
    ) {
        SetScaleType(scaleType) // set scaleType
        GlideApp
            .with(imageView.context)
            .asBitmap()
            .load(url)
            .placeholder(placeHolder)
            .apply(options)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any,
                    target: Target<Bitmap?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (imageLoadingListener != null) imageLoadingListener.onLoaded(resource, null)
                    return false
                }
            })
            .error(errorDrawable)
            .into(imageView)
    }

    /**
     * @param scaleType ( Constant.ImageScaleType.CENTER_CROP , Constant.ImageScaleType.FIT_CENTER )
     */
    private fun SetScaleType(scaleType: Int) {
        options = RequestOptions()
        if (scaleType != 0) {
            if (scaleType == Constant.ImageScaleType.CENTER_CROP) {
                options!!.centerCrop()
            } else if (scaleType == Constant.ImageScaleType.FIT_CENTER) {
                options!!.fitCenter()
            }
        }
    }

    /**
     * TODO : If you don't want to scale image then scaleType = 0;
     *
     * @param url
     * @param imageView
     * @param errorDrawable
     * @param progressBar
     * @param scaleType            ( Constant.ImageScaleType.CENTER_CROP , Constant.ImageScaleType.FIT_CENTER )
     * @param imageLoadingListener ( If you don't want to get success and error of image loading then pass null )
     */
    fun loadImageWithProgressBar(
        url: String?, imageView: ImageView, errorDrawable: Drawable?,
        progressBar: ProgressBar, scaleType: Int, imageLoadingListener: ImageLoadingListener?
    ) {
        SetScaleType(scaleType)
        GlideApp
            .with(imageView.context)
            .asBitmap()
            .load(url)
            .apply(options)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any,
                    target: Target<Bitmap?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (imageLoadingListener != null) imageLoadingListener.onLoaded(resource, null)
                    imageView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .error(errorDrawable)
            .into(imageView)
    }

    /**
     * TODO : If you don't want to scale image then scaleType = 0;
     *
     * @param imageView
     * @param fileName
     * @param placeHolder
     * @param errorDrawable
     * @param scaleType            ( Constant.ImageScaleType.CENTER_CROP , Constant.ImageScaleType.FIT_CENTER )
     * @param imageLoadingListener ( If you don't want to get success and error of image loading then pass null )
     */
    fun loadImageFromFile(
        imageView: ImageView, fileName: File?, placeHolder: Drawable?,
        errorDrawable: Drawable?, scaleType: Int, imageLoadingListener: ImageLoadingListener?
    ) {
        SetScaleType(scaleType)
        GlideApp
            .with(imageView.context)
            .asBitmap()
            .load(Uri.fromFile(fileName))
            .placeholder(placeHolder)
            .error(errorDrawable)
            .apply(options)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any,
                    target: Target<Bitmap?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (imageLoadingListener != null) imageLoadingListener.onLoaded(resource, null)
                    return false
                }
            })
            .error(errorDrawable)
            .into(imageView)
    }

    fun loadCircularImageFromFile(imageView: ImageView, file: File?) {
        Glide.with(imageView.context)
            .load(file)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

    /**
     * TODO : If you don't want to scale image then scaleType = 0;
     *
     * @param url
     * @param view
     * @param transformation       ( Constant.TransformationType.CenterCropTransform , Constant.TransformationType.CircleCropTransform , Constant.TransformationType.BlurTransformation , Constant.TransformationType.RoundedCornersTransformation , Constant.TransformationType.ColorFilterTansformation, Constant.TransformationType.GrayscaleTransformation )
     * @param placeHolder
     * @param errorDrawable
     * @param scaleType            ( Constant.ImageScaleType.CENTER_CROP , Constant.ImageScaleType.FIT_CENTER )
     * @param corner
     * @param margin
     * @param color
     * @param border
     * @param imageLoadingListener ( If you don't want to get success and error of image loading then pass null )
     */
    fun loadImageUsingGlideTransformation(
        url: String?,
        view: ImageView,
        transformation: String?,
        placeHolder: Drawable?,
        errorDrawable: Drawable?,
        scaleType: Int,
        corner: Int,
        margin: Int,
        color: String?,
        border: Int,
        imageLoadingListener: ImageLoadingListener?
    ) {
        SetScaleType(scaleType)
        when (transformation) {
            Constant.TransformationType.CENTERCROP_TRANSFORM -> GlideApp.with(view.context)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.centerCropTransform()) // center crop image
                .placeholder(placeHolder)
                .error(errorDrawable)
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Bitmap?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any,
                        target: Target<Bitmap?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onLoaded(
                            resource,
                            null
                        )
                        return false
                    }
                })
                .into(view)
            Constant.TransformationType.CIRCLECROP_TRANSFORM -> GlideApp.with(view.context)
                .load(url)
                .apply(RequestOptions.circleCropTransform()) // circle crop image
                .placeholder(placeHolder)
                .error(errorDrawable)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onLoaded(
                            null,
                            errorDrawable
                        )
                        return false
                    }
                })
                .into(view)
            Constant.TransformationType.BLUR_TRANSFORMATION -> GlideApp.with(view.context)
                .load(url)
                .transform(BlurTransformation(view.context, border)) // for blur image
                .placeholder(placeHolder)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onLoaded(
                            null,
                            errorDrawable
                        )
                        return false
                    }
                })
                .error(errorDrawable)
                .into(view)
            Constant.TransformationType.ROUNDED_CORNERS_TRANSFORMATION -> GlideApp.with(view.context)
                .load(url)
                .apply(
                    RequestOptions.bitmapTransform(
                        RoundedCornersTransformation(
                            view.context,
                            corner,
                            margin,
                            color,
                            border
                        )
                    )
                ) //for the round Corner Image
                .placeholder(placeHolder)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onLoaded(
                            null,
                            errorDrawable
                        )
                        return false
                    }
                })
                .error(errorDrawable)
                .into(view)
            Constant.TransformationType.COLOR_FILTER_TRANSFORMATION -> GlideApp.with(view.context)
                .load(url)
                .transform(ColorFilterTransformation(Color.parseColor(color))) // for color image
                .placeholder(placeHolder)
                .error(errorDrawable)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onLoaded(
                            null,
                            errorDrawable
                        )
                        return false
                    }
                })
                .into(view)
            Constant.TransformationType.GRAYSCALE_TRANSFORMATION -> GlideApp.with(view.context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(GrayscaleTransformation(view.context)))
                .placeholder(placeHolder)
                .error(errorDrawable)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onFailed(e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (imageLoadingListener != null) imageLoadingListener.onLoaded(
                            null,
                            errorDrawable
                        )
                        return false
                    }
                })
                .into(view)
            else -> {
            }
        }
    }
}