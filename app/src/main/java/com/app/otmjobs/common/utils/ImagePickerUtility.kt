package com.app.otmjobs.common.utils

import android.app.Activity
import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.app.imagepickers.models.FileWithPath
import com.app.imagepickers.pickiT.CallBackTask
import com.app.imagepickers.pickiT.DownloadAsyncTask
import com.app.imagepickers.pickiT.PickiTCallbacks
import com.app.imagepickers.pickiT.Utils
import com.app.otmjobs.BuildConfig
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.activity.CropImageActivity
import java.io.File
import java.util.*

class ImagePickerUtility(context: Context, listener: PickiTCallbacks, activity: Activity) : CallBackTask {
    private lateinit var context: Context
    private var pickiTCallbacks: PickiTCallbacks? = null
    private var isDriveFile = false
    private var isMsfDownload = false
    private var isFromUnknownProvider = false
    private var asyntask: DownloadAsyncTask? = null
    private var unknownProviderCalledBefore = false
    private lateinit var mActivity: Activity
    var multiplePaths = ArrayList<String>()
    private var wasMultipleFileSelected = false
    private var countMultiple = 0
    private var driveCountRef = 0
    private var enableProc = true

    private val requestCode = 0
    private var mCurrentPhotoPath: String? = null

    init {
        this.context = context
        this.pickiTCallbacks = listener
        mActivity = activity
    }

    fun pickImage(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivity.startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode)
    }

    fun pickPdf(requestCode: Int) {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivity.startActivityForResult(intent, requestCode)
    }

    fun pickFile(requestCode: Int) {
        val mimeTypes = arrayOf("image/*", "application/pdf", "application/vnd.ms-excel")
        val intent = Intent()
        intent.type = "image/*|application/pdf|application/vnd.ms-excel"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        mActivity.startActivityForResult(intent, requestCode)
    }

    fun openCamera(requestCode: Int) {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                mActivity.startActivityForResult(takePictureIntent, requestCode)
            } else {
                var fileWithPath: FileWithPath = AppUtils.createImageFile(
                    "",
                    AppConstants.Type.CAMERA,
                    AppConstants.FileExtension.JPG
                )
                mCurrentPhotoPath = fileWithPath.filePath
                val photoURI = FileProvider.getUriForFile(
                    mActivity, BuildConfig.APPLICATION_ID.toString() + ".provider",
                    fileWithPath.file
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                mActivity.startActivityForResult(takePictureIntent, requestCode)
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
        (mActivity as BaseActivity).moveActivityForResult(
            context!!,
            CropImageActivity::class.java,
            false,
            false,
            AppConstants.IntentKey.REQUEST_CROP_IMAGE,
            bundle
        )
    }

    fun getMultiplePaths(clipData: ClipData) {
        wasMultipleFileSelected = true
        countMultiple = clipData.itemCount
        for (i in 0 until clipData.itemCount) {
            val imageUri = clipData.getItemAt(i).uri
            getPath(imageUri, Build.VERSION.SDK_INT)
        }
        if (!isDriveFile) {
            pickiTCallbacks!!.PickiTonMultipleCompleteListener(multiplePaths, true, "")
            multiplePaths.clear()
            wasMultipleFileSelected = false
            wasUriReturnedCalledBefore = false
            wasPreExecuteCalledBefore = false
        }
    }

    fun getPath(uri: Uri, APILevel: Int) {
        val returnedPath: String
        if (APILevel >= 19) {
            var docId: String? = null
            // This is only used when a file is selected from a sub-directory inside the Downloads folder
            // and when the Uri returned has the msf: prefix
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    docId = DocumentsContract.getDocumentId(uri)
                }
            } catch (e: java.lang.Exception) {
                // Ignore
            }
            // Drive file was selected
            if (isOneDrive(uri) || isDropBox(uri) || isGoogleDrive(uri)) {
                isDriveFile = true
                downloadFile(uri)
            } else if (docId != null && docId.startsWith("msf")) {
                val fileName: String = Utils.getFilePath(context, uri)!!
                try {
                    val file = File(
                        Environment.getExternalStorageDirectory()
                            .toString() + "/Download/" + fileName
                    )
                    // If the file exists in the Downloads directory
                    // we can return the path directly
                    if (file.exists()) {
                        pickiTCallbacks!!.PickiTonCompleteListener(
                            file.absolutePath,
                            false,
                            false,
                            true,
                            ""
                        )
                    } else {
                        if (enableProc) {
                            val parcelFileDescriptor: ParcelFileDescriptor?
                            try {
                                parcelFileDescriptor =
                                    context!!.contentResolver.openFileDescriptor(uri, "r")
                                val fd = parcelFileDescriptor!!.fd
                                val pid = Process.myPid()
                                val mediaFile = "/proc/$pid/fd/$fd"
                                val file1 = File(mediaFile)
                                if (file1.exists() && file1.canRead() && file1.canWrite()) {
                                    pickiTCallbacks!!.PickiTonCompleteListener(
                                        file1.absolutePath,
                                        false,
                                        false,
                                        true,
                                        ""
                                    )
                                } else {
                                    isMsfDownload = true
                                    downloadFile(uri)
                                }
                            } catch (e: java.lang.Exception) {
                                isMsfDownload = true
                                downloadFile(uri)
                            }
                        } else {
                            isMsfDownload = true
                            downloadFile(uri)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    isMsfDownload = true
                    downloadFile(uri)
                }
            } else {
                returnedPath = Utils.getRealPathFromURI_API19(context, uri)!!
                //Get the file extension
                val mime = MimeTypeMap.getSingleton()
                val subStringExtension = returnedPath.substring(returnedPath.lastIndexOf(".") + 1)
                val extensionFromMime =
                    mime.getExtensionFromMimeType(context.contentResolver.getType(uri))

                // Path is null
                if (returnedPath == null || returnedPath == "") {
                    // This can be caused by two situations
                    // 1. The file was selected from a third party app and the data column returned null (for example EZ File Explorer)
                    // Some file providers (like EZ File Explorer) will return a URI as shown below:
                    // content://es.fileexplorer.filebrowser.ezfilemanager.externalstorage.documents/document/primary%3AFolderName%2FNameOfFile.mp4
                    // When you try to read the _data column, it will return null, without trowing an exception
                    // In this case the file need to copied/created a new file in the temporary folder
                    // 2. There was an error
                    // In this case call PickiTonCompleteListener and get/provide the reason why it failed

                    //We first check if it was called before, avoiding multiple calls
                    if (!unknownProviderCalledBefore) {
                        unknownProviderCalledBefore = true
                        if (uri.scheme != null && uri.scheme == ContentResolver.SCHEME_CONTENT) {
                            //Then we check if the _data colomn returned null
                            if (Utils.errorReason() != null && Utils.errorReason()
                                    .equals("dataReturnedNull")
                            ) {
                                isFromUnknownProvider = true
                                //Copy the file to the temporary folder
                                downloadFile(uri)
                                return
                            } else if (Utils.errorReason() != null && Utils.errorReason()
                                    ?.contains("column '_data' does not exist")!!
                            ) {
                                isFromUnknownProvider = true
                                //Copy the file to the temporary folder
                                downloadFile(uri)
                                return
                            } else if (Utils.errorReason() != null && Utils.errorReason()
                                    .equals("uri")
                            ) {
                                isFromUnknownProvider = true
                                //Copy the file to the temporary folder
                                downloadFile(uri)
                                return
                            }
                        }
                    }
                    //Else an error occurred, get/set the reason for the error
                    pickiTCallbacks!!.PickiTonCompleteListener(
                        returnedPath,
                        false,
                        false,
                        false,
                        Utils.errorReason()
                    )
                } else {
                    // This can be caused by two situations
                    // 1. The file was selected from an unknown provider (for example a file that was downloaded from a third party app)
                    // 2. getExtensionFromMimeType returned an unknown mime type for example "audio/mp4"
                    //
                    // When this is case we will copy/write the file to the temp folder, same as when a file is selected from Google Drive etc.
                    // We provide a name by getting the text after the last "/"
                    // Remember if the extension can't be found, it will not be added, but you will still be able to use the file
                    //Todo: Add checks for unknown file extensions
                    if (subStringExtension != "jpeg" && subStringExtension != extensionFromMime && uri.scheme != null && uri.scheme == ContentResolver.SCHEME_CONTENT) {
                        isFromUnknownProvider = true
                        downloadFile(uri)
                        return
                    }

                    // Path can be returned, no need to make a "copy"
                    if (wasMultipleFileSelected) {
                        multiplePaths.add(returnedPath)
                    } else {
                        pickiTCallbacks!!.PickiTonCompleteListener(
                            returnedPath,
                            false,
                            false,
                            true,
                            ""
                        )
                    }
                }
            }
        } else {
            //Todo: Test API <19
            returnedPath = Utils.getRealPathFromURI_BelowAPI19(context, uri)!!
            pickiTCallbacks!!.PickiTonCompleteListener(returnedPath, false, false, true, "")
        }
    }


    // When selecting a file inside a sub-directory, some devices (Android 10>) will use the msf: provider
    // which we can't convert to a path without copying it to the applications directory.
    // For that, we make use of the /proc/ protocol.
    // Since this has not been tested fully, I'm adding the option to disable it.
    // I don't think this is necessary, but I'm giving the option nevertheless.
    fun enableProcProtocol(shouldEnable: Boolean) {
        enableProc = shouldEnable
    }

    // Create a new file from the Uri that was selected
    private fun downloadFile(uri: Uri) {
        asyntask = DownloadAsyncTask(uri, context, this, mActivity)
        asyntask!!.execute()
    }

    // End the "copying" of the file
    fun cancelTask() {
        if (asyntask != null) {
            asyntask!!.cancel(true)
            deleteTemporaryFile(context)
        }
    }

    fun wasLocalFileSelected(uri: Uri): Boolean {
        return !isDropBox(uri) && !isGoogleDrive(uri) && !isOneDrive(uri)
    }

    // Check different providers
    private fun isDropBox(uri: Uri): Boolean {
        return uri.toString().toLowerCase().contains("content://com.dropbox.")
    }

    private fun isGoogleDrive(uri: Uri): Boolean {
        return uri.toString().toLowerCase().contains("com.google.android.apps")
    }

    private fun isOneDrive(uri: Uri): Boolean {
        return uri.toString().toLowerCase().contains("com.microsoft.skydrive.content")
    }

    // PickiT callback Listeners
    private var wasUriReturnedCalledBefore = false

    override fun PickiTonUriReturned() {
        if (wasMultipleFileSelected) {
            if (!wasUriReturnedCalledBefore) {
                pickiTCallbacks!!.PickiTonUriReturned()
                wasUriReturnedCalledBefore = true
            }
        } else {
            pickiTCallbacks!!.PickiTonUriReturned()
        }
    }

    private var wasPreExecuteCalledBefore = false

    override fun PickiTonPreExecute() {
        if (wasMultipleFileSelected || isMsfDownload) {
            if (!wasPreExecuteCalledBefore) {
                wasPreExecuteCalledBefore = true
                pickiTCallbacks!!.PickiTonStartListener()
            }
        } else {
            pickiTCallbacks!!.PickiTonUriReturned()
        }
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        pickiTCallbacks!!.PickiTonProgressUpdate(progress)
    }

    override fun PickiTonPostExecute(
        path: String?,
        wasDriveFile: Boolean,
        wasSuccessful: Boolean,
        reason: String?
    ) {
        unknownProviderCalledBefore = false
        if (wasSuccessful) {
            if (wasMultipleFileSelected) {
                driveCountRef++
                multiplePaths.add(path!!)
                if (driveCountRef == countMultiple) {
                    wasPreExecuteCalledBefore = false
                    wasUriReturnedCalledBefore = false
                    pickiTCallbacks!!.PickiTonMultipleCompleteListener(multiplePaths, true, "")
                    multiplePaths.clear()
                    wasUriReturnedCalledBefore = false
                    wasPreExecuteCalledBefore = false
                }
            } else {
                if (isDriveFile) {
                    pickiTCallbacks!!.PickiTonCompleteListener(path, true, false, true, "")
                } else if (isFromUnknownProvider) {
                    pickiTCallbacks!!.PickiTonCompleteListener(path, false, true, true, "")
                } else if (isMsfDownload) {
                    pickiTCallbacks!!.PickiTonCompleteListener(path, false, true, true, "")
                }
            }
        } else {
            if (isDriveFile) {
                pickiTCallbacks!!.PickiTonCompleteListener(path, true, false, false, reason)
            } else if (isFromUnknownProvider) {
                pickiTCallbacks!!.PickiTonCompleteListener(path, false, true, false, reason)
            }
        }
    }

    // Delete the temporary folder
    fun deleteTemporaryFile(context: Context) {
        val folder = context.getExternalFilesDir("Temp")
        if (folder != null) {
            if (deleteDirectory(folder)) {
                Log.i("PickiT ", " deleteDirectory was called")
            }
        }
    }

    private fun deleteDirectory(path: File): Boolean {
        if (path.exists()) {
            val files = path.listFiles() ?: return false
            for (file in files) {
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    val wasSuccessful = file.delete()
                    if (wasSuccessful) {
                        Log.i("Deleted ", "successfully")
                    }
                }
            }
        }
        return path.delete()
    }
}