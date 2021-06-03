package com.app.otmjobs.common.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.imagepickers.utils.FileUtils
import com.app.otmjobs.R
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityCropImageBinding
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import com.isseiaoki.simplecropview.callback.SaveCallback
import java.io.File

class CropImageActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCropImageBinding
    private lateinit var mContext: Context
    private lateinit var sourceUri: Uri
    private var cropX: Int = 0
    private var cropY: Int = 0
    private var fileExtension: String = ""
    private val KEY_FRAME_RECT = "FrameRect"
    private lateinit var mCompressFormat: CompressFormat
    private lateinit var mFrameRect: RectF

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setStatusBarColor()
        mContext = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop_image)
        setupToolbar("", true)
        if (savedInstanceState != null) {
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT)!!
        }
        getIntentData()

        binding.imgRotateLeft.setOnClickListener(this)
        binding.imgRotateRight.setOnClickListener(this)
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            sourceUri = Uri.parse(intent.getStringExtra(AppConstants.IntentKey.IMAGE_URI))
            cropX = intent.getIntExtra(AppConstants.IntentKey.CROP_RATIO_X, 0)
            cropY = intent.getIntExtra(AppConstants.IntentKey.CROP_RATIO_Y, 0)
            fileExtension = intent.getStringExtra(AppConstants.IntentKey.FILE_EXTENSION)!!
            mCompressFormat =
                if (fileExtension == AppConstants.FileExtension.JPG) CompressFormat.JPEG else if (fileExtension == AppConstants.FileExtension.PNG) CompressFormat.PNG else CompressFormat.JPEG
            if (sourceUri != null) {
                binding.cropImageView.load(sourceUri)
                    .initialFrameRect(mFrameRect)
                    .useThumbnail(true)
                    .execute(mLoadCallback)
                binding.cropImageView.setCustomRatio(cropX, cropY)
            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgRotateLeft -> binding.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D)
            R.id.imgRotateRight -> binding.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.done_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> {
                showProgressDialog(mContext, "")
                binding.cropImageView.crop(sourceUri).execute(mCropCallback)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val mLoadCallback: LoadCallback = object : LoadCallback {
        override fun onSuccess() {}
        override fun onError(e: Throwable) {}
    }

    private val mCropCallback: CropCallback = object : CropCallback {
        override fun onSuccess(cropped: Bitmap) {
            binding.cropImageView.save(cropped)
                .compressFormat(mCompressFormat)
                .execute(createSaveUri(cropped), mSaveCallback)
        }

        override fun onError(e: Throwable) {}
    }

    private val mSaveCallback: SaveCallback = object : SaveCallback {
        override fun onSuccess(outputUri: Uri) {
            hideProgressDialog()
            val i = Intent()
            i.putExtra(AppConstants.IntentKey.IMAGE_URI, outputUri.toString())
            setResult(1, i)
            finish()
        }

        override fun onError(e: Throwable) {
            hideProgressDialog()
        }
    }

    fun createSaveUri(bitmap: Bitmap): Uri {
        val newPath: String = AppUtils.getFilePathFromBitmap(mContext, bitmap, fileExtension)
        return FileUtils.getUri(File(newPath))!!
    }
}