package com.app.imagepickers.pickiT

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.provider.OpenableColumns
import android.util.Log
import java.io.*
import java.lang.ref.WeakReference

public class DownloadAsyncTask(uri: Uri, context: Context, callback: CallBackTask, activity: Activity) : AsyncTask<Uri, Int, String>() {

    private var mUri: Uri? = null
    private var callback: CallBackTask? = null
    private var mContext: WeakReference<Context>? = null
    private var pathPlusName: String? = null
    private var folder: File? = null
    private var returnCursor: Cursor? = null
    private var `is`: InputStream? = null
    private var errorReason = ""
    private var activityReference: WeakReference<Activity>? = null

    init {
        this.mUri = uri
        this.mContext = WeakReference(context)
        this.callback = callback
        activityReference = WeakReference(activity)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        callback!!.PickiTonUriReturned()
    }

    override fun doInBackground(vararg params: Uri?): String {

        var file: File? = null
        var size = -1

        val context = mContext!!.get()
        if (context != null) {
            folder = context.getExternalFilesDir("Temp")
            if (folder != null && !folder!!.exists()) {
                if (folder!!.mkdirs()) {
                    Log.i("PickiT : ", "Temp folder createdd")
                }
            }
            returnCursor = context.contentResolver.query(mUri!!, null, null, null, null)
            try {
                `is` = context.contentResolver.openInputStream(mUri!!)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        // File is now available

        // File is now available
        activityReference!!.get()!!.runOnUiThread { callback!!.PickiTonPreExecute() }

        try {
            try {
                if (returnCursor != null && returnCursor!!.moveToFirst()) {
                    if (mUri!!.scheme != null) if (mUri!!.scheme == "content") {
                        val sizeIndex = returnCursor!!.getColumnIndex(OpenableColumns.SIZE)
                        size = returnCursor!!.getLong(sizeIndex).toInt()
                    } else if (mUri!!.scheme == "file") {
                        val ff = File(mUri!!.path!!)
                        size = ff.length().toInt()
                    }
                }
            } finally {
                if (returnCursor != null) returnCursor!!.close()
            }
            pathPlusName = folder.toString() + "/" + getFileName(mUri!!, mContext!!.get())
            file = File(folder.toString() + "/" + getFileName(mUri!!, mContext!!.get()))
            val bis = BufferedInputStream(`is`)
            val fos = FileOutputStream(file)
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            while (bis.read(data).also { count = it } != -1) {
                if (!isCancelled) {
                    total += count.toLong()
                    if (size != -1) {
                        try {
                            publishProgress((total * 100 / size).toInt())
                        } catch (e: java.lang.Exception) {
                            Log.i("PickiT -", "File size is less than 1")
                            publishProgress(0)
                        }
                    }
                    fos.write(data, 0, count)
                }
            }
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            Log.e("Pickit IOException = ", e.message!!)
            errorReason = e.message!!
        }

        return file!!.absolutePath
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val post = values[0]!!
        callback!!.PickiTonProgressUpdate(post)
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result == null) {
            callback!!.PickiTonPostExecute(pathPlusName, true, false, errorReason)
        } else {
            callback!!.PickiTonPostExecute(pathPlusName, true, true, "")
        }
    }

    private fun getFileName(uri: Uri, context: Context?): String {
        var result: String? = null
        if (uri.scheme != null) {
            if (uri.scheme == "content") {
                val cursor = context!!.contentResolver.query(uri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            assert(result != null)
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}

