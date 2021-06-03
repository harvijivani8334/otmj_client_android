package com.app.imagepickers.utils

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.app.imagepickers.models.FileWithPath
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {
    val TAG = ImageUtils::class.java.simpleName

    /**
     * get scaled bitmap
     * @param filePath  local file path
     * @param maxWidth  scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     * bitmap width is maxWidth while bitmap height is maxWidth * ratio
     * @param maxHeight scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     * bitmap height is maxHeight while bitmap width is maxHeight / ratio.
     * @return scaled bitmap
     */

//    object ImageUtil {
//        /**
//         * @param type
//         * @return
//         * @throws IOException
//         */
//        @Throws(IOException::class)
//
//    }

    fun createImageFile(type: String?): FileWithPath {
        val fileWithPath = FileWithPath()
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                type
            ), "Camera"
        )
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        val mCurrentPhotoPath = "file:" + image.absolutePath
        fileWithPath.file = image
        fileWithPath. filePath = mCurrentPhotoPath
        return fileWithPath
    }

    fun getScaledBitmap(filePath: String?, maxWidth: Int, maxHeight: Int): Bitmap? {
        val decodeOptions = BitmapFactory.Options()
        val bitmap: Bitmap?
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, decodeOptions)
        val actualWidth = decodeOptions.outWidth
        val actualHeight = decodeOptions.outHeight
        Log.d(
            TAG,
            "Actual width: $actualWidth, actual height: $actualHeight"
        )
        // Then compute the dimensions we would ideally like to decode to.
        val desiredWidth = getResizedDimension(
            maxWidth, maxHeight,
            actualWidth, actualHeight
        )
        val desiredHeight = getResizedDimension(
            maxHeight, maxWidth,
            actualHeight, actualWidth
        )
        Log.d(
            TAG,
            "Desired width: $desiredWidth, desired height: $desiredHeight"
        )

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
            findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight)
        val tempBitmap = BitmapFactory.decodeFile(filePath, decodeOptions)
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.width > desiredWidth ||
                    tempBitmap.height > desiredHeight)
        ) {
            bitmap = Bitmap.createScaledBitmap(
                tempBitmap,
                desiredWidth, desiredHeight, true
            )
            tempBitmap.recycle()
        } else {
            bitmap = tempBitmap
        }
        return bitmap
    }

    /**
     * get scaled bitmap
     * @param imageResId image resource id
     * @param maxWidth  scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     * bitmap width is maxWidth while bitmap height is maxWidth * ratio
     * @param maxHeight scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     * bitmap height is maxHeight while bitmap width is maxHeight / ratio.
     * @return scaled bitmap
     */
    fun getScaledBitmap(context: Context, imageResId: Int, maxWidth: Int, maxHeight: Int): Bitmap? {
        val decodeOptions = BitmapFactory.Options()
        val bitmap: Bitmap?
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, imageResId, decodeOptions)
        val actualWidth = decodeOptions.outWidth
        val actualHeight = decodeOptions.outHeight
        Log.d(
            TAG,
            "Actual width: $actualWidth, actual height: $actualHeight"
        )

        // Then compute the dimensions we would ideally like to decode to.
        val desiredWidth = getResizedDimension(
            maxWidth, maxHeight,
            actualWidth, actualHeight
        )
        val desiredHeight = getResizedDimension(
            maxHeight, maxWidth,
            actualHeight, actualWidth
        )
        Log.d(
            TAG,
            "Desired width: $desiredWidth, desired height: $desiredHeight"
        )

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
            findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight)
        val tempBitmap = BitmapFactory.decodeResource(context.resources, imageResId, decodeOptions)
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.width > desiredWidth ||
                    tempBitmap.height > desiredHeight)
        ) {
            bitmap = Bitmap.createScaledBitmap(
                tempBitmap,
                desiredWidth, desiredHeight, true
            )
            tempBitmap.recycle()
        } else {
            bitmap = tempBitmap
        }
        return bitmap
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth Actual width of the bitmap
     * @param actualHeight Actual height of the bitmap
     * @param desiredWidth Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    private fun findBestSampleSize(
        actualWidth: Int, actualHeight: Int, desiredWidth: Int, desiredHeight: Int
    ): Int {
        val wr = actualWidth.toDouble() / desiredWidth
        val hr = actualHeight.toDouble() / desiredHeight
        val ratio = Math.min(wr, hr)
        var n = 1.0f
        while (n * 2 <= ratio) {
            n *= 2f
        }
        return n.toInt()
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary Maximum size of the primary dimension (i.e. width for
     * max width), or zero to maintain aspect ratio with secondary
     * dimension
     * @param maxSecondary Maximum size of the secondary dimension, or zero to
     * maintain aspect ratio with primary dimension
     * @param actualPrimary Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private fun getResizedDimension(
        maxPrimary: Int, maxSecondary: Int, actualPrimary: Int,
        actualSecondary: Int
    ): Int {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            val ratio = maxSecondary.toDouble() / actualSecondary.toDouble()
            return (actualPrimary * ratio).toInt()
        }
        if (maxSecondary == 0) {
            return maxPrimary
        }
        val ratio = actualSecondary.toDouble() / actualPrimary.toDouble()
        var resized = maxPrimary
        if (resized * ratio > maxSecondary) {
            resized = (maxSecondary / ratio).toInt()
        }
        return resized
    }

    /**
     * get actual image dimension
     * @param imagePath local file path
     * @return
     */
    fun getActualImageDimension(imagePath: String?): IntArray {
        val imageSize = IntArray(2)
        val decodeOptions = BitmapFactory.Options()
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, decodeOptions)
        val actualWidth = decodeOptions.outWidth
        val actualHeight = decodeOptions.outHeight
        imageSize[0] = actualWidth
        imageSize[1] = actualHeight
        return imageSize
    }

    /**
     * get actual image dimension
     * @param imageResId image resource id
     * @return
     */
    fun getActualImageDimension(context: Context, imageResId: Int): IntArray {
        val imageSize = IntArray(2)
        val decodeOptions = BitmapFactory.Options()
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, imageResId, decodeOptions)
        val actualWidth = decodeOptions.outWidth
        val actualHeight = decodeOptions.outHeight
        imageSize[0] = actualWidth
        imageSize[1] = actualHeight
        return imageSize
    }

    private fun getDesiredImageDimension(
        imagePath: String,
        maxWidth: Int,
        maxHeight: Int
    ): IntArray {
        val desiredImageDimension = IntArray(2)
        val actualImageDimension = getActualImageDimension(imagePath)
        Log.d(
            TAG,
            "Actual width: " + actualImageDimension[0] + ", actual height: " + actualImageDimension[1]
        )
        val maxPrimary: Int
        val maxSecondary: Int
        if (actualImageDimension[0] >= actualImageDimension[1]) {
            maxPrimary = maxWidth
            maxSecondary = 0
            desiredImageDimension[0] = getResizedDimension(
                maxPrimary, maxSecondary,
                actualImageDimension[0], actualImageDimension[1]
            )
            desiredImageDimension[1] = getResizedDimension(
                maxSecondary, maxPrimary,
                actualImageDimension[1], actualImageDimension[0]
            )
        } else {
            maxPrimary = maxHeight
            maxSecondary = 0
            desiredImageDimension[1] = getResizedDimension(
                maxPrimary, maxSecondary,
                actualImageDimension[1], actualImageDimension[0]
            )
            desiredImageDimension[0] = getResizedDimension(
                maxPrimary, maxSecondary,
                actualImageDimension[0], actualImageDimension[1]
            )
        }
        Log.d(
            TAG,
            "Desired width: " + desiredImageDimension[0] + ", desired height: " + desiredImageDimension[1]
        )
        return desiredImageDimension
    }

    private fun getDesiredImageDimension(
        context: Context,
        imageResId: Int,
        maxWidth: Int,
        maxHeight: Int
    ): IntArray {
        val desiredImageDimension = IntArray(2)
        val actualImageDimension = getActualImageDimension(context, imageResId)
        Log.d(
            TAG,
            "Actual width: " + actualImageDimension[0] + ", actual height: " + actualImageDimension[1]
        )
        val maxPrimary: Int
        val maxSecondary: Int
        if (actualImageDimension[0] >= actualImageDimension[1]) {
            maxPrimary = maxWidth
            maxSecondary = 0
            desiredImageDimension[0] = getResizedDimension(
                maxPrimary, maxSecondary,
                actualImageDimension[0], actualImageDimension[1]
            )
            desiredImageDimension[1] = getResizedDimension(
                maxSecondary, maxPrimary,
                actualImageDimension[1], actualImageDimension[0]
            )
        } else {
            maxPrimary = maxHeight
            maxSecondary = 0
            desiredImageDimension[1] = getResizedDimension(
                maxPrimary, maxSecondary,
                actualImageDimension[1], actualImageDimension[0]
            )
            desiredImageDimension[0] = getResizedDimension(
                maxPrimary, maxSecondary,
                actualImageDimension[0], actualImageDimension[1]
            )
        }
        Log.d(
            TAG,
            "Desired width: " + desiredImageDimension[0] + ", desired height: " + desiredImageDimension[1]
        )
        return desiredImageDimension
    }

    /**
     * compress the image file, create a scaled compressed image file, and overwrite the origin one.
     * @param path  origin image file path
     * @param maxWidth
     * @param maxHeight
     * @param quality
     */
    fun compress(path: String?, maxWidth: Int, maxHeight: Int, quality: Int) {
        val out: FileOutputStream
        try {
            val scaledBitmap = getScaledBitmap(path, maxWidth, maxHeight)
            val rotatedBitmap = scaledBitmap!!
            out = FileOutputStream(path)
            val mutableBitmap: Bitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap //rotateBitmap(getBitmapDegree(path), scaledBitmap);
                .compress(Bitmap.CompressFormat.JPEG, quality, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * compress the image file, create a scaled compressed image file, and overwrite the origin one.
     * @param maxWidth
     * @param maxHeight
     * @param quality
     */
    fun compress(
        originPath: String?,
        outputPath: String?,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ) {
        val out: FileOutputStream
        try {
            val scaledBitmap = getScaledBitmap(originPath, maxWidth, maxHeight)
            val rotatedBitmap = rotateBitmap(getBitmapDegree(originPath), scaledBitmap)
            out = FileOutputStream(outputPath)
            val mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * add water mark at the left top of image.
     * @param context
     * @param srcPath   local image file path
     * @param watermarkRes  watermark resource
     * @param maxWidth scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     * bitmap width is maxWidth while bitmap height is maxWidth * ratio
     * @param maxHeight scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     * bitmap height is maxHeight while bitmap width is maxHeight / ratio.
     * @param quality compress quality
     */
    // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    fun watermark(
        context: Context,
        srcPath: String?,
        watermarkRes: Int,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ) {
        val out: FileOutputStream
        try {
            val scaledBitmap = getScaledBitmap(srcPath, maxWidth, maxHeight)
            val rotatedBitmap = rotateBitmap(getBitmapDegree(srcPath), scaledBitmap)
            val scaledWatermark = getScaledBitmap(context, watermarkRes, maxWidth, maxHeight)
            out = FileOutputStream(srcPath)
            val mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)
            canvas.drawBitmap(scaledWatermark!!, 0f, 0f, null)

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "Add watermark result in OOM")
            e.printStackTrace()
        }
    }

    /**
     * add watermark at the right bottom of the image
     * @param context
     * @param srcPath   local image file path
     * @param watermarkRes  watermark resource
     * @param maxWidth scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     * bitmap width is maxWidth while bitmap height is maxWidth * ratio
     * @param maxHeight scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     * bitmap height is maxHeight while bitmap width is maxHeight / ratio.
     * @param quality compress quality
     */
    fun watermarkAtRightBottom(
        context: Context,
        srcPath: String?,
        watermarkRes: Int,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ) {
        val out: FileOutputStream
        try {
            val scaledBitmap = getScaledBitmap(srcPath, maxWidth, maxHeight)
            val rotatedBitmap = rotateBitmap(getBitmapDegree(srcPath), scaledBitmap)
            val scaledWatermark = getScaledBitmap(context, watermarkRes, maxWidth, maxHeight)
            out = FileOutputStream(srcPath)
            val left = rotatedBitmap.width - scaledWatermark!!.width
            val top = rotatedBitmap.height - scaledWatermark.height
            val mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)
            canvas.drawBitmap(scaledWatermark, left.toFloat(), top.toFloat(), null)

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "Add watermark result in OOM")
            e.printStackTrace()
        }
    }

    /**
     * get bitmap degree, you may get an rotated photo when you take a picture in some devices.
     * @param path local image file path
     * @return
     */
    fun getBitmapDegree(path: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path!!)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /**
     * rotate bitmap
     * @param angle rotate angle
     * @param bitmap origin bitmap
     * @return rotated bitmap
     */
    fun rotateBitmap(angle: Int, bitmap: Bitmap?): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * create a copied image.
     * @param context
     * @param photoUri origin image uri
     * @param outputPath   output image uri.
     * @return true if successfully compressed to the specified stream.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyBitmapFile(context: Context, photoUri: Uri?, outputPath: String?): Boolean {
        // Load image from path
        val input = context.contentResolver.openInputStream(photoUri!!)

        // compress it
        val bitmapOrigin = BitmapFactory.decodeStream(input) ?: return false
        // save to file
        val output = FileOutputStream(outputPath)
        return bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, 100, output)
    }

    fun centerCropBitmap(srcBmp: Bitmap): Bitmap? {
        var dstBmp: Bitmap? = null
        dstBmp = if (srcBmp.width >= srcBmp.height) {
            Bitmap.createBitmap(
                srcBmp,
                srcBmp.width / 2 - srcBmp.height / 2,
                0,
                srcBmp.height,
                srcBmp.height
            )
        } else {
            Bitmap.createBitmap(
                srcBmp,
                0,
                srcBmp.height / 2 - srcBmp.width / 2,
                srcBmp.width,
                srcBmp.width
            )
        }
        return dstBmp
    }

    fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), (
                    bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}
