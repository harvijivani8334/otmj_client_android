package com.app.otmjobs.authentication.ui.activity


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.app.imagepickers.models.FileWithPath
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.FileUtils
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.BuildConfig
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.CountryResponse
import com.app.otmjobs.authentication.data.model.User
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.callback.SelectAttachmentListener
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.callback.SelectPostCodeListener
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.data.model.PostCodeInfo
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.fragment.CountryFlagDialog
import com.app.otmjobs.common.ui.fragment.SearchPostCodeDialog
import com.app.otmjobs.common.ui.fragment.SelectAttachmentDialog
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityEditProfileBinding
import com.app.utilities.callback.OnDateSetListener
import com.app.utilities.fragments.DatePickerFragment
import com.app.utilities.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : BaseActivity(), View.OnClickListener, SelectItemListener,
    SelectPostCodeListener, PermissionCallbacks, SelectAttachmentListener, OnDateSetListener {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var mContext: Context
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    lateinit var user: User
    private lateinit var countryResponse: CountryResponse
    private lateinit var searchPostCodeDialog: SearchPostCodeDialog
    private var isValidPhone = false
    private var currentPhotoPath: String = ""
    private var imagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        setStatusBarColor()
        setupToolbar(getString(R.string.edit_profile), true)
        mContext = this
        editProfileObservers()
        getCustomerDetailsObservers()
        setCountriesObservers()

        binding.txtSave.setOnClickListener(this)
        binding.txtUploadPhoto.setOnClickListener(this)
        binding.imgUserProfile.setOnClickListener(this)
        binding.txtSearch.setOnClickListener(this)
        binding.edtPostCode.setOnClickListener(this)
        binding.routExtensionView.setOnClickListener(this)
        binding.edtBirthDate.setOnClickListener(this)

        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val phoneNumber =
                    StringHelper.removeZeroOnFirstLetter(binding.edtPhone.text.toString())
                if (phoneNumber.length != 10) {
                    binding.edtPhoneExtension.visibility = View.GONE
                }
                if (!isValidPhone && phoneNumber.length == 10) {
                    isValidPhone = true
                    binding.edtPhoneExtension.visibility = View.VISIBLE
                    binding.edtPhone.setText(phoneNumber)
                    binding.edtPhone.setSelection(binding.edtPhone.text!!.length)
                } else {
                    isValidPhone = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        getCustomerData()
    }

    private fun getCustomerData() {
        showProgressDialog(mContext, "")
        authenticationViewModel.getCustomerDetails(AppUtils.getUserPreference(mContext)!!.customer_id)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSave -> {
                if (valid()) {
                    showProgressDialog(mContext, "")
                    user.device_type = AppConstants.DEVICE_TYPE.toString()
                    user.device_token = AppUtils.getDeviceToken()
                    authenticationViewModel.editProfileResponse(user, imagePath)
                }
            }
            R.id.edtPostCode, R.id.txtSearch -> showPostCodeDialog()
            R.id.routExtensionView ->
                showFlagListDialog(
                    countryResponse.data,
                    AppConstants.DialogIdentifier.SELECT_COUNTRY_FLAG
                )
            R.id.txtUploadPhoto, R.id.imgUserProfile -> checkPermission()
            R.id.edtBirthDate -> {
                val c = Calendar.getInstance()
                c[c[Calendar.YEAR] - 18, c[Calendar.MONTH]] = c[Calendar.DAY_OF_MONTH]
                if (!StringHelper.isEmpty(binding.edtBirthDate.getText().toString())) {
                    val date = DateHelper.changeDateFormat(
                        binding.edtBirthDate.getText().toString(),
                        DateFormatsConstants.DD_MMM_YYYY_SPACE,
                        DateFormatsConstants.YYYY_MM_DD_DASH
                    )
                    showDatePicker(0, c.time.time, AppConstants.DialogIdentifier.DOB_PICKER, date)
                } else {
                    showDatePicker(0, c.time.time, AppConstants.DialogIdentifier.DOB_PICKER, null)
                }
            }
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (!ValidationUtil.isEmptyEditText(user.first_name)) {
            binding.edtFirstName.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtFirstName,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isEmptyEditText(user.last_name)) {
            binding.edtLastName.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtLastName,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!ValidationUtil.isEmptyEditText(user.email_address)) {
            if (ValidationUtil.isValidEmail(binding.edtEmailId.text.toString())) {
                binding.edtEmailId.error = null
            } else {
                ValidationUtil.setErrorIntoEditText(
                    binding.edtEmailId,
                    mContext.getString(R.string.error_valid_email)
                )
                valid = false
                return valid
            }
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtEmailId,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (user.country_id == 0) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtPhone,
                mContext.getString(R.string.error_empty_country)
            )
            valid = false
            return valid
        } else {
            binding.edtPhone.error = null
        }

        if (!ValidationUtil.isEmptyEditText(
                StringHelper.removeZeroOnFirstLetter(user.phone_number)
            )
        ) {
            if (!ValidationUtil.isValidPhoneNumberRange(
                    StringHelper.removeZeroOnFirstLetter(
                        user.phone_number
                    )
                )
            ) {
                ValidationUtil.setErrorIntoEditText(
                    binding.edtPhone,
                    mContext.getString(R.string.error_phone_number_min_length)
                )
                valid = false
                return valid
            } else {
                if (ValidationUtil.isPhoneNumberStartWithZero(user.phone_number)) {
                    ValidationUtil.setErrorIntoEditText(
                        binding.edtPhone,
                        mContext.getString(R.string.error_phone_number_can_not_start_with_zero)
                    )
                    valid = false
                    return valid
                } else {
                    binding.edtPhone.error = null
                }
            }
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtPhone,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (ValidationUtil.isEmptyEditText(user.date_of_birth)) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtBirthDate,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtBirthDate.error = null
        }

        if (ValidationUtil.isEmptyEditText(user.pin_code)) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtPostCode,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtPostCode.error = null
        }

        if (ValidationUtil.isEmptyEditText(user.street_address)) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtStreet,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtStreet.error = null
        }

        if (ValidationUtil.isEmptyEditText(user.city)) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtCity,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtCity.error = null
        }

        return valid
    }

    private fun setCountriesObservers() {
        authenticationViewModel.countryResponse.observe(this) { response ->
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
                        countryResponse = response
                        for (i in countryResponse.data.indices) {
                            if (user.country_id == countryResponse.data[i].id) {
                                binding.edtPhoneExtension.setText(countryResponse.data[i].extension)
                                user.phone_extension_id = countryResponse.data[i].id.toString()
                                setCountryFlag(countryResponse.data[i].flag_name)
                                break
                            }
                        }
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun getCustomerDetailsObservers() {
        authenticationViewModel.customerDetailsResponse.observe(this) { response ->
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext, null,
                        mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                        null, false, null, 0
                    )
                } else {
                    if (response.IsSuccess) {
                        authenticationViewModel.getCountriesResponse()
                        binding.routMainView.visibility = View.VISIBLE
                        user = User()
                        user = response.info
                        binding.data = user
                        if (!StringHelper.isEmpty(response.info.date_of_birth))
                            binding.edtBirthDate.setText(
                                DateHelper.changeDateFormat(
                                    response.info.date_of_birth,
                                    DateFormatsConstants.YYYY_MM_DD_DASH,
                                    DateFormatsConstants.DD_MMM_YYYY_SPACE
                                )
                            )
                        setUserImage(user.image!!)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun editProfileObservers() {
        authenticationViewModel.loginResponse.observe(this) { response ->
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
                        val user: User = AppUtils.getUserPreference(mContext)!!
                        user.first_name = response.info.first_name
                        user.last_name = response.info.last_name
                        user.date_of_birth = response.info.date_of_birth
                        user.country_id = response.info.country_id
                        user.phone_extension_id = response.info.phone_extension_id
                        user.phone_number = response.info.phone_number
                        user.street_address = response.info.street_address
                        user.city = response.info.city
                        user.image = response.info.image
                        user.pin_code = response.info.pin_code
                        AppUtils.setUserPreference(mContext, user)
                        finish()
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setCountryFlag(flagUrl: String) {
        if (!StringHelper.isEmpty(flagUrl))
            GlideUtil.loadImage(
                flagUrl,
                binding.imgCountryFlag,
                null,
                null,
                Constant.ImageScaleType.FIT_CENTER,
                null
            )
    }

    private fun setUserImage(image: String) {
        AppUtils.setUserImage(mContext, image, binding.imgUserProfile)
    }

    private fun setUserImageFromFile(file: File) {
        GlideUtil.loadCircularImageFromFile(binding.imgUserProfile, file)
    }

    private fun showPostCodeDialog() {
        val fm = (mContext as BaseActivity).supportFragmentManager
        searchPostCodeDialog = SearchPostCodeDialog(this, this, 0)
        searchPostCodeDialog.show(fm, "dropdownFragment")
    }

    private fun showFlagListDialog(list: MutableList<ModuleInfo>, identity: Int) {
        val fm = supportFragmentManager
        val countryFlagDialog = CountryFlagDialog(
            this,
            list,
            this,
            identity
        )
        countryFlagDialog.show(fm, "countryFlagDialog")
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

    override fun onSelectPostCode(info: PostCodeInfo, tag: Int) {
//        binding.edtPostCode.setText(info.postcode)
//        binding.edtAddress.setText(info.summaryline.replace(", ", ",\n"))
//        user.pin_code = info.postcode

        binding.edtPostCode.setText(info.postcode)
        binding.edtStreet.setText(info.addressline1 + ", " + info.addressline2 + ", " + info.posttown)
        binding.edtCity.setText(info.county)

        searchPostCodeDialog.dismiss()
    }

    override fun onSelectItem(position: Int, action: Int) {
        if (action == AppConstants.DialogIdentifier.SELECT_COUNTRY_FLAG) {
            user.country_id = countryResponse.data[position].id
            user.phone_extension_id = countryResponse.data[position].id.toString()
            binding.edtPhoneExtension.setText(countryResponse.data[position].extension)
            setCountryFlag(countryResponse.data[position].flag_name)
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
                val realPath: String = FileUtils.getPath(mContext, data.data)!!
                Log.e("test", "realPath:$realPath")
                if (!StringHelper.isEmpty(realPath)) {
                    imagePath = realPath
                    setUserImageFromFile(File(realPath));
                }
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
                    Log.e("test", "realPath:$realPath")
                    imagePath = realPath
                    setUserImageFromFile(File(realPath));
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun showDatePicker(minDate: Long, maxDate: Long, tag: String, selDate: String?) {
        val newFragment: DialogFragment = DatePickerFragment.newInstance(
            minDate,
            maxDate,
            selDate,
            DateFormatsConstants.YYYY_MM_DD_DASH,
            tag
        )
        newFragment.show(supportFragmentManager, tag)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        if (view!!.tag.toString() == AppConstants.DialogIdentifier.DOB_PICKER) {
            val dobDate = Calendar.getInstance()
            dobDate[year, month] = day
            val dateFormat = SimpleDateFormat(AppConstants.defaultDateFormat, Locale.US)
            binding.edtBirthDate.setText(dateFormat.format(dobDate.time))
            val dateFormat1 = SimpleDateFormat(DateFormatsConstants.YYYY_MM_DD_DASH, Locale.US)
            user.date_of_birth = dateFormat1.format(dobDate.time)

        }
    }
}