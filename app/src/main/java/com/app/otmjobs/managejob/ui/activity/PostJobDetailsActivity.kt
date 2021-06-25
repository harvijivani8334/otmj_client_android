package com.app.otmjobs.managejob.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.app.imagepickers.models.FileWithPath
import com.app.imagepickers.utils.FileUtils
import com.app.otmjobs.BuildConfig
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectAttachmentListener
import com.app.otmjobs.common.callback.SelectDateRangeListener
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.data.model.FileDetail
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.fragment.SelectAttachmentDialog
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.PopupMenuHelper
import com.app.otmjobs.databinding.ActivityPjJobDetailsBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.data.model.TradeInfo
import com.app.otmjobs.managejob.ui.adapter.PostJobPhotosListAdapter
import com.app.otmjobs.managejob.ui.fragment.SelectDateRangeDialog
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import com.app.utilities.utils.ToastHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class PostJobDetailsActivity : BaseActivity(), View.OnClickListener, SelectItemListener,
    SelectDateRangeListener, EasyPermissions.PermissionCallbacks, SelectAttachmentListener {
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var binding: ActivityPjJobDetailsBinding
    private lateinit var mContext: Context;
    private var listCategories: MutableList<TradeInfo> = ArrayList()
    private var imageList: MutableList<JobImageInfo> = ArrayList()
    private lateinit var postJobRequest: PostJobRequest
    private lateinit var adapterPhotos: PostJobPhotosListAdapter
    private var currentPhotoPath: String = ""
    private var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pj_job_details)
        setStatusBarColor()
        mContext = this
        setupToolbar(getString(R.string.post_job), true)
        getTradesObservers()
        deleteJobImageObservers()

        binding.edtYourBudget.hint = String.format(
            getString(R.string.display_budget_hint),
            getString(R.string.your_budget),
            AppConstants.CURRENCY
        )
        binding.edtYourBudget.floatingLabelText = String.format(
            getString(R.string.display_budget_hint),
            getString(R.string.your_budget),
            AppConstants.CURRENCY
        )

        binding.edtCategory.setOnClickListener(this)
        binding.edtStartFrom.setOnClickListener(this)
        binding.txtNext.setOnClickListener(this)

        getIntentData()
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            if (intent.hasExtra(AppConstants.IntentKey.POST_JOB_DATA)) {
                postJobRequest =
                    Parcels.unwrap(intent.getParcelableExtra(AppConstants.IntentKey.POST_JOB_DATA))
                loadTradesDataAPI()
            } else {
                postJobRequest = PostJobRequest()
                listCategories.addAll(intent.getParcelableArrayListExtra(AppConstants.IntentKey.CATEGORY_DATA)!!)
                postJobRequest.trade_id = intent.getIntExtra(AppConstants.IntentKey.CATEGORY_ID, 0)
                postJobRequest.trade_name =
                    intent.getStringExtra(AppConstants.IntentKey.CATEGORY_TITLE)!!
            }

            val detail = JobImageInfo()
            detail.file_name = ""
            imageList.add(0, detail)

            for (i in postJobRequest.images!!.indices)
                imageList.add(postJobRequest.images!![i])

            setPhotosListAdapter()
            binding.data = postJobRequest
        }
    }

    private fun loadTradesDataAPI() {
        showProgressDialog(mContext, "")
        manageJobViewModel.getTradesResponse()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edtCategory -> {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    AppConstants.IntentKey.CATEGORY_DATA,
                    listCategories as ArrayList
                )
                bundle.putInt(AppConstants.IntentKey.CATEGORY_ID, postJobRequest.trade_id!!)
                bundle.putString(AppConstants.IntentKey.CATEGORY_TITLE, postJobRequest.trade_name!!)
                val intent = Intent(this, SelectTradeActivity::class.java)
                intent.putExtras(bundle)
                resultSelectCategory.launch(intent)
            }
            R.id.edtStartFrom -> {
                val listJobStartFrom = resources.getStringArray(R.array.listJobStartFrom)
                val list: MutableList<ModuleInfo> = ArrayList()

                for (i in listJobStartFrom.indices) {
                    val info = ModuleInfo()
                    info.name = listJobStartFrom[i]
                    list.add(info)
                }

                PopupMenuHelper.showPopupMenu(
                    mContext,
                    v,
                    list,
                    AppConstants.Action.SELECT_JOB_START_FROM,
                    this
                )
            }
            R.id.txtNext -> {
                if (valid()) {
                    val bundle = Bundle()
                    bundle.putParcelable(
                        AppConstants.IntentKey.POST_JOB_DATA,
                        Parcels.wrap(postJobRequest)
                    )
                    bundle.putParcelableArrayList(
                        AppConstants.IntentKey.POST_JOB_PHOTOS,
                        imageList as java.util.ArrayList
                    )
                    val intent = Intent(mContext, PostJobContactInformationActivity::class.java)
                    intent.putExtras(bundle)
                    resultAddJob.launch(intent)
                }
            }
        }
    }

    private fun setPhotosListAdapter() {
        binding.rvPhotos.setHasFixedSize(true)
        adapterPhotos = PostJobPhotosListAdapter(mContext, imageList, this)
        binding.rvPhotos.adapter = adapterPhotos
    }

    private fun valid(): Boolean {
        var valid = true
        if (!StringHelper.isEmpty(postJobRequest.trade_name)) {
            binding.edtCategory.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtCategory,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!StringHelper.isEmpty(postJobRequest.job_title)) {
            binding.edtJobTitle.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtJobTitle,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!StringHelper.isEmpty(postJobRequest.job_description)) {
            binding.edtJobDescription.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtJobDescription,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!StringHelper.isEmpty(postJobRequest.budget)) {
            binding.edtYourBudget.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtYourBudget,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!StringHelper.isEmpty(postJobRequest.expected_start_time)) {
            binding.edtStartFrom.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtStartFrom,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }
        return valid
    }

    private fun getTradesObservers() {
        manageJobViewModel.getTradesResponse.observe(this) { response ->
            hideProgressDialog()
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext, null,
                        mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                        null, false, null, 0
                    )
                } else {
                    if (response.IsSuccess) {
                        listCategories.addAll(response.info)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun deleteJobImageObservers() {
        manageJobViewModel.baseResponse.observe(this) { response ->
            hideProgressDialog()
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext, null,
                        mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                        null, false, null, 0
                    )
                } else {
                    if (response.IsSuccess) {

                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onSelectItem(position: Int, action: Int) {
        when (action) {
            AppConstants.Action.SELECT_JOB_START_FROM -> {
                if (position == 4) {
                    showSelectDateRangeDialog()
                } else {
                    val listJobStartFrom = resources.getStringArray(R.array.listJobStartFrom)
                    postJobRequest.expected_start_time = (position + 1).toString()
                    binding.edtStartFrom.setText(listJobStartFrom[position])
                }
            }
            AppConstants.Action.ADD_PHOTO -> {
                checkPermission()
            }
            AppConstants.Action.DELETE_PHOTO -> {
                if (adapterPhotos.list[position].job_image_id > 0) {
                    isUpdate = true
                    manageJobViewModel.deleteJobImageResponse(
                        adapterPhotos.list[position].job_image_id
                    )
                }
                imageList.removeAt(position)
                adapterPhotos.notifyDataSetChanged()
            }
        }
    }

    private fun showSelectDateRangeDialog() {
        val fm = supportFragmentManager
        val selectDateRangeDialog = SelectDateRangeDialog(
            this@PostJobDetailsActivity,
            this,
        )
        selectDateRangeDialog.show(fm, "selectDateRangeDialog")
    }

    override fun onSelectDate(startDate: String?, endDate: String?) {
        binding.edtStartFrom.setText(
            String.format(
                getString(R.string.display_date_range),
                startDate,
                endDate
            )
        )
        postJobRequest.expected_start_time = String.format(
            getString(R.string.display_date_range),
            AppUtils.getApiDateFormat(startDate),
            AppUtils.getApiDateFormat(endDate)
        )
    }

    var resultSelectCategory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                postJobRequest.trade_id =
                    data!!.getIntExtra(AppConstants.IntentKey.CATEGORY_ID, 0);
                postJobRequest.trade_name =
                    data.getStringExtra(AppConstants.IntentKey.CATEGORY_TITLE)!!
                binding.edtCategory.setText(postJobRequest.trade_name)
            }
        }

    var resultAddJob =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    private fun showSelectAttachmentDialog() {
        val fm = (mContext as BaseActivity).supportFragmentManager
        val selectAttachmentDialog =
            SelectAttachmentDialog(this, this, 0, true, true)
        selectAttachmentDialog.show(fm, "selectAttachmentDialog")
    }

    private fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun checkPermission() {
        if (hasPermission()) {
            showSelectAttachmentDialog()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.msg_storage_permission),
                AppConstants.IntentKey.EXTERNAL_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        showSelectAttachmentDialog()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onSelectAttachment(action: Int) {
        if (action == AppConstants.Type.SELECT_PHOTOS) {
            onSelectFromGallery()
        } else if (action == AppConstants.Type.SELECT_FROM_CAMERA) {
            onSelectFromCamera()
        }
    }

    private fun onSelectFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        selectPhotosRequest.launch(intent)
    }

    private fun onSelectFromCamera() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var fileWithPath: FileWithPath = AppUtils.createImageFile(
                "",
                AppConstants.Type.CAMERA,
                AppConstants.FileExtension.JPG
            )
            currentPhotoPath = fileWithPath.filePath
            val photoURI = FileProvider.getUriForFile(
                mContext, BuildConfig.APPLICATION_ID + ".provider",
                fileWithPath.file
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            selectFromCameraRequest.launch(takePictureIntent)
        } catch (e: Exception) {

        }
    }

    var selectPhotosRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent = result.data!!
                if (data.clipData != null) {
                    val count: Int =
                        data.clipData!!.itemCount //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    if (isLeftImageCount(count)) {
                        for (i in 0 until count) {
                            val imageUri: Uri = data.clipData!!.getItemAt(i).uri;
                            val realPath: String = FileUtils.getPath(mContext, imageUri)!!
                            if (!StringHelper.isEmpty(realPath))
                                setImageData(realPath)
                        }
                    }
                } else {
                    val realPath: String = FileUtils.getPath(mContext, data.data)!!
                    if (!StringHelper.isEmpty(realPath) && isLeftImageCount(1)) {
                        setImageData(realPath)
                    }
                }
                adapterPhotos.notifyDataSetChanged()
            }
        }

    var selectFromCameraRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val file = File(
                        FileUtils.getPath(mContext, Uri.parse(currentPhotoPath))!!
                    )
                    var realPath = ""
                    val fileWithPath: FileWithPath =
                        AppUtils.compressImage(file.absolutePath, File(file.absolutePath))!!

                    if (fileWithPath.uri != null)
                        realPath = FileUtils.getPath(
                            mContext,
                            Uri.parse(fileWithPath.filePath)
                        )!! else
                        file.path

                    if (isLeftImageCount(1)) {
                        val fileDetail = JobImageInfo()
                        fileDetail.file_name = realPath
                        imageList.add(fileDetail)
                        adapterPhotos.notifyDataSetChanged()
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun setImageData(realPath: String) {
        if (!StringHelper.isEmpty(realPath)) {
            val fileWithPath = AppUtils.compressImage(
                realPath,
                File(realPath)
            )
            if (fileWithPath?.uri != null) {
                val fileDetail = JobImageInfo()
                fileDetail.file_name = fileWithPath.file.absolutePath
                imageList.add(fileDetail)
            } else {
                val fileDetail = JobImageInfo()
                fileDetail.file_name = realPath
                imageList.add(fileDetail)
            }
        }
    }

    override fun onBackPressed() {
        if (isUpdate)
            setResult(Activity.RESULT_OK)
        finish()
    }

    private fun isLeftImageCount(inputCount: Int): Boolean {
        return if (((imageList.size - 1) + inputCount) <= 10) {
            true
        } else {
            ToastHelper.normal(
                mContext, mContext.getString(R.string.error_max_image_limit),
                Toast.LENGTH_SHORT,
                false
            )
            false
        }
    }
}