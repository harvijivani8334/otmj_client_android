package com.app.otmjobs.common.ui.activity

import android.content.Context
import android.content.CursorLoader
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.app.otmjobs.R
import com.app.otmjobs.common.data.model.FileDetail
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.adapter.GalleryImageListAdapter
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityGalleryImageListBinding
import com.app.utilities.utils.DateFormatsConstants
import kotlinx.coroutines.*
import java.io.File

class GalleryImageListActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGalleryImageListBinding
    private lateinit var mContext: Context;
    private var adapter: GalleryImageListAdapter? = null
    private var fileDetailList: MutableList<FileDetail> = ArrayList()
    private var cursorLoader: CursorLoader? = null
    private val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.DATE_MODIFIED
    )
    private val selection =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
    private var sortOrder: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery_image_list)
        setStatusBarColor()
        mContext = this
        setupToolbar(getString(R.string.select_photos), true)
        sortOrder =
            MediaStore.Files.FileColumns.DATE_MODIFIED + " " + getString(R.string.desc_order)


        val uri = MediaStore.Files.getContentUri("external")
        cursorLoader = CursorLoader(
            mContext,
            uri,
            projection,
            selection,
            null,  // Selection args selectionArgsPdf (none).
            sortOrder // Sort order.
        )

//        getIntentData()

        GlobalScope.launch(Dispatchers.IO) {
            Log.e("test","GlobalScope.launch");
            loadImages()
        }
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.routChatTab -> setupTab(0)
        }
    }

    suspend fun loadImages() = coroutineScope {
        Log.e("test","loadImages");
        showProgressDialog(mContext, "")
        val data = async(Dispatchers.IO) { // <- extension on current scope
            try {
                val allFiles = cursorLoader!!.loadInBackground()
                if (allFiles != null && allFiles.count > 0) {
                    val count = allFiles.count
                    for (i in 0 until count) {
                        allFiles.moveToPosition(i)
                        val filePath =
                            allFiles.getString(allFiles.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        //String type = allFiles.getString(allFiles.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
                        val file = File(filePath)
                        val extension = MimeTypeMap.getFileExtensionFromUrl(file.toString())
                        val displayName =
                            allFiles.getString(allFiles.getColumnIndex(MediaStore.Files.FileColumns.TITLE)) + "." + extension
                        val fileSize: String = AppUtils.formatFileSize(
                            allFiles.getLong(
                                allFiles.getColumnIndex(OpenableColumns.SIZE)
                            ).toDouble()
                        )
                        val fileDate: String = AppUtils.convertLongDateToString(
                            file.lastModified(),
                            DateFormatsConstants.DD_MM_YYYY_DASH
                        )!!
                        val fileDetail = FileDetail()
                        fileDetail.displayName = displayName
                        fileDetail.fileDate = fileDate
                        fileDetail.fileSize = fileSize
                        fileDetail.filePath = filePath
                        fileDetail.fileSizeDouble =
                            allFiles.getLong(allFiles.getColumnIndex(OpenableColumns.SIZE))
                        fileDetailList.add(fileDetail)
                    }
                }
            } catch (e: java.lang.Exception) {

            }
        }

        withContext(Dispatchers.Main) {
            val layoutManager = GridLayoutManager(mContext, 4)
            binding.rvGalleryImages.layoutManager = layoutManager
            adapter = GalleryImageListAdapter(mContext, fileDetailList)
            binding.rvGalleryImages.adapter = adapter
            hideProgressDialog()
            val result = data.await()
//            display(result)
        }
    }

}