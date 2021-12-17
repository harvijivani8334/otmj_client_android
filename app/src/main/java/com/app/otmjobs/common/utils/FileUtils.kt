package com.app.otmjobs.common.utils

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.DownloadFileListener
import com.app.utilities.utils.ProgressDialogHelper
import java.io.File

object FileUtils {
    fun download(mContext: Context, url: String, listener: DownloadFileListener) {
        val progressDialogHelper = ProgressDialogHelper(mContext)

        val directory = File(Environment.DIRECTORY_DOCUMENTS)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(Environment.getExternalStorageDirectory().toString(), directory.toString()+"/"+url.substring(url.lastIndexOf("/") + 1))
        if (file.exists()) {
            listener.onFileDownloaded(file.absolutePath, "", 0)
        } else {
            progressDialogHelper.showCircularProgressDialog()
            val downloadManager =
                mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(url)

            val request = DownloadManager.Request(downloadUri).apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(url.substring(url.lastIndexOf("/") + 1))
                    .setDescription("")
                    .setDestinationInExternalPublicDir(
                        directory.toString(),
                        url.substring(url.lastIndexOf("/") + 1)
                    )
            }

            val downloadId = downloadManager.enqueue(request)
            val query = DownloadManager.Query().setFilterById(downloadId)
            Thread(Runnable {
                var downloading = true
                while (downloading) {
                    val cursor: Cursor = downloadManager.query(query)
                    cursor.moveToFirst()
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        progressDialogHelper.hideCircularProgressDialog()
                        val filePath = File(Environment.getExternalStorageDirectory().toString(), directory.toString()+"/"+url.substring(url.lastIndexOf("/") + 1))
                        listener.onFileDownloaded(filePath.absolutePath, "", 0)
                        Log.e(
                            "test",
                            "Image downloaded successfully in $directory" + File.separator + url.substring(
                                url.lastIndexOf("/") + 1
                            )
                        )
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        progressDialogHelper.hideCircularProgressDialog()
                    }
                    cursor.close()
                }
            }).start()
        }
    }

    fun viewPdf(mContext: Context, path: String?) {
        if (path != null) {
            val file = File(path)
            Log.e("test","absolutePath2222:"+file.absolutePath);
            val uri =
                FileProvider.getUriForFile(mContext, mContext.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                mContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.msg_no_application_found),
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: NullPointerException) {
            }
        }
    }
}

