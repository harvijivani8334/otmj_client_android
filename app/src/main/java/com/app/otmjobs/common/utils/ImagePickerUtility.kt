package com.app.otmjobs.common.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.app.imagepickers.models.FileWithPath
import com.app.otmjobs.BuildConfig
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.activity.CropImageActivity

class ImagePickerUtility(private val a: Activity) {
    private val requestCode = 0

    var currentPhotoPath: String? = null
        private set

    fun pickImage(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        a.startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode)
    }

    fun pickPdf(requestCode: Int) {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        a.startActivityForResult(intent, requestCode)
    }

    fun pickFile(requestCode: Int) {
        val mimeTypes = arrayOf("image/*", "application/pdf", "application/vnd.ms-excel")
        val intent = Intent()
        intent.type = "image/*|application/pdf|application/vnd.ms-excel"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        a.startActivityForResult(intent, requestCode)
    }

    fun openCamera(requestCode: Int) {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                a.startActivityForResult(takePictureIntent, requestCode)
            } else {
                var fileWithPath: FileWithPath = AppUtils.createImageFile(
                    "",
                    AppConstants.Type.CAMERA,
                    AppConstants.FileExtension.JPG
                )
                currentPhotoPath = fileWithPath.filePath
                val photoURI = FileProvider.getUriForFile(
                    a, BuildConfig.APPLICATION_ID.toString() + ".provider",
                    fileWithPath.file
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                a.startActivityForResult(takePictureIntent, requestCode)
            }
        } catch (e: Exception) {

        }
    }

    fun startCrop(sourceUri: Uri, ratioX: Int, ratioY: Int, outputExtension: String) {
        val bundle = Bundle()
        bundle.putString(AppConstants.IntentKey.IMAGE_URI, sourceUri.toString())
        bundle.putInt(AppConstants.IntentKey.CROP_RATIO_X, ratioX)
        bundle.putInt(AppConstants.IntentKey.CROP_RATIO_Y, ratioY)
        bundle.putString(AppConstants.IntentKey.FILE_EXTENSION, outputExtension)
        (a as BaseActivity).moveActivityForResult(
            a,
            CropImageActivity::class.java,
            false,
            false,
            AppConstants.IntentKey.REQUEST_CROP_IMAGE,
            bundle
        )
    }

}