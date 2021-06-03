package com.app.otmjobs.managejob.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.CountryResponse
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.callback.LocationUpdateCallBack
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.callback.SelectPostCodeListener
import com.app.otmjobs.common.data.model.FileDetail
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.data.model.PostCodeInfo
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.fragment.CountryFlagDialog
import com.app.otmjobs.common.ui.fragment.SearchPostCodeDialog
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.LocationHelper
import com.app.otmjobs.databinding.ActivityPjContactInfoBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class PostJobContactInformationActivity : BaseActivity(), View.OnClickListener,
    SelectItemListener, SelectPostCodeListener,
    LocationUpdateCallBack {
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var binding: ActivityPjContactInfoBinding
    private lateinit var mContext: Context;
    private lateinit var postJobRequest: PostJobRequest
    private var isValidPhone = false
    private var isEdit = false
    private lateinit var searchPostCodeDialog: SearchPostCodeDialog
    private var mLocationHelper: LocationHelper? = null
    private lateinit var countryResponse: CountryResponse
    private var imageList: MutableList<JobImageInfo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pj_contact_info)
        setStatusBarColor()
        mContext = this
        setupToolbar(getString(R.string.post_job), true)
        setPhoneExtensionObservers()
        setCountriesObservers()
        addJobObservers()

        binding.edtPostCode.setOnClickListener(this)
        binding.txtSearch.setOnClickListener(this)
        binding.routExtensionView.setOnClickListener(this)
        binding.edtCountry.setOnClickListener(this)
        binding.edtCountry2.setOnClickListener(this)
        binding.edtPostCode2.setOnClickListener(this)
        binding.txtNext.setOnClickListener(this)
        binding.cbUseAnotherAddress.setOnCheckedChangeListener { button: CompoundButton, isChecked: Boolean ->
            if (isChecked)
                binding.routAnotherAddress.visibility = View.VISIBLE
            else
                binding.routAnotherAddress.visibility = View.GONE
        }

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

        getIntentData()
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            postJobRequest =
                Parcels.unwrap(intent.getParcelableExtra(AppConstants.IntentKey.POST_JOB_DATA))
            if (postJobRequest.job_id != 0) {
                isEdit = true
            } else {
                mLocationHelper = LocationHelper(this)
                mLocationHelper!!.setLocationUpdateListener(this)
                if(!mLocationHelper!!.isGPSEnabled)
                    authenticationViewModel.getPhoneExtensionResponse(AppConstants.defaultCountryCode)
                startLocationUpdate()
            }
            binding.data = postJobRequest

            val list: MutableList<JobImageInfo> = ArrayList()
            list.addAll(intent.getParcelableArrayListExtra(AppConstants.IntentKey.POST_JOB_PHOTOS)!!)
            list.removeAt(0)

            for (i in list.indices) {
                if (list[i].job_image_id == 0)
                    imageList.add(list[i])
            }

            Log.e("test", "imageList.size:" + imageList.size);
            getCountriesAPI()
        }
    }

    private fun getCountriesAPI() {
        showProgressDialog(mContext, "")
        authenticationViewModel.getCountriesResponse()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtNext -> {
                if (valid()) {
                    postJobRequest.language = "English"
                    postJobRequest.device_type = AppConstants.DEVICE_TYPE.toString()
                    postJobRequest.device_token = AppUtils.getDeviceToken()
                    showProgressDialog(mContext, "")
                    if (isEdit)
                        manageJobViewModel.updateJobResponse(postJobRequest)
                    else
                        manageJobViewModel.addJobResponse(postJobRequest)
                }
            }
            R.id.edtPostCode, R.id.txtSearch ->
                showPostCodeDialog(AppConstants.DialogIdentifier.SELECT_POST_CODE)
            R.id.edtPostCode2, R.id.txtSearch2 ->
                showPostCodeDialog(AppConstants.DialogIdentifier.SELECT_POST_CODE2)
            R.id.routExtensionView, R.id.edtCountry ->
                showFlagListDialog(
                    countryResponse.data,
                    AppConstants.DialogIdentifier.SELECT_COUNTRY_FLAG
                )
            R.id.edtCountry2 ->
                showFlagListDialog(
                    countryResponse.data,
                    AppConstants.DialogIdentifier.SELECT_COUNTRY2_FLAG
                )
        }
    }

    private fun setPhoneExtensionObservers() {
        authenticationViewModel.phoneExtensionResponse.observe(this) { response ->
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext, null,
                        mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                        null, false, null, 0
                    )
                } else {
                    if (response.IsSuccess) {
                        if (response.country_id != 0) {
                            binding.edtPhoneExtension.setText(response.phone_extension_name)
                            binding.edtCountry.setText(response.country)
                            binding.edtCountry2.setText(response.country)
                            postJobRequest.country_id = response.country_id
                            postJobRequest.second_country_id = response.country_id
                            setCountryFlag(response.flag_name)
                        }
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
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
                        if (isEdit) {
                            for (i in countryResponse.data.indices) {
                                if (postJobRequest.country_id == countryResponse.data[i].id) {
                                    binding.edtPhoneExtension.setText(countryResponse.data[i].extension)
                                    setCountryFlag(countryResponse.data[i].flag_name)
                                    break
                                }
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

    private fun addJobObservers() {
        manageJobViewModel.addJobResponse.observe(this) { response ->
            if (imageList.size == 0)
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
                        if (postJobRequest.job_id == 0)
                            postJobRequest.job_id = response.info!!.job_id

                        if (imageList.size > 0) {
                            manageJobViewModel.addJobImageResponse(
                                postJobRequest.job_id!!,
                                imageList.get(0).file_name!!
                            )
                            imageList.removeAt(0)
                        } else {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (postJobRequest.country_id == 0) {
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
                StringHelper.removeZeroOnFirstLetter(postJobRequest.phone_number!!)
            )
        ) {
            if (!ValidationUtil.isValidPhoneNumberRange(
                    StringHelper.removeZeroOnFirstLetter(
                        postJobRequest.phone_number!!
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
                if (ValidationUtil.isPhoneNumberStartWithZero(postJobRequest.phone_number!!)) {
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

        if (!StringHelper.isEmpty(postJobRequest.pin_code)) {
            binding.edtPostCode.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtPostCode,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!StringHelper.isEmpty(postJobRequest.street_address)) {
            binding.edtStreet.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtStreet,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (!StringHelper.isEmpty(postJobRequest.city)) {
            binding.edtCity.error = null
        } else {
            ValidationUtil.setErrorIntoEditText(
                binding.edtCity,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        }

        if (postJobRequest.country_id == 0) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtCountry,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtCountry.error = null
        }

        return valid
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

    private fun showPostCodeDialog(tag: Int) {
        val fm = (mContext as BaseActivity).supportFragmentManager
        searchPostCodeDialog = SearchPostCodeDialog(this, this, tag)
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

    override fun onSelectPostCode(info: PostCodeInfo, tag: Int) {
        if (tag == AppConstants.DialogIdentifier.SELECT_POST_CODE) {

            binding.edtPostCode.setText(info.postcode)
            binding.edtStreet.setText(info.addressline1 + ", " + info.addressline2 + ", " + info.posttown)
            binding.edtCity.setText(info.county)

            searchPostCodeDialog.dismiss()
        } else if (tag == AppConstants.DialogIdentifier.SELECT_POST_CODE2) {

            binding.edtPostCode2.setText(info.postcode)
            binding.edtStreet2.setText(info.addressline1 + ", " + info.addressline2 + ", " + info.posttown)
            binding.edtCity2.setText(info.county)

            searchPostCodeDialog.dismiss()
        }
    }

    private fun startLocationUpdate() {
        mLocationHelper?.startLocationUpdates()
    }

    fun stopLocationUpdate() {
        mLocationHelper?.stopLocationUpdates()
    }

    override fun locationUpdate(location: Location) {
        if (StringHelper.isEmpty(postJobRequest.pin_code)) {
            val address =
                mLocationHelper!!.getGeocoderAddress(location.latitude, location.longitude)
            if (address != null) {
                if (!StringHelper.isEmpty(address.countryCode)) {
                    authenticationViewModel.getPhoneExtensionResponse(address.countryCode)
                    stopLocationUpdate()
                }
            }
        }
    }

    override fun onSelectItem(position: Int, action: Int) {
        if (action == AppConstants.DialogIdentifier.SELECT_COUNTRY_FLAG) {
            postJobRequest.country_id = countryResponse.data[position].id
            binding.edtPhoneExtension.setText(countryResponse.data[position].extension)
            binding.edtCountry.setText(countryResponse.data[position].name)
            setCountryFlag(countryResponse.data[position].flag_name)
        } else if (action == AppConstants.DialogIdentifier.SELECT_COUNTRY2_FLAG) {
            postJobRequest.second_country_id = countryResponse.data[position].id
            binding.edtCountry2.setText(countryResponse.data[position].name)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.IntentKey.LOCATION_SETTING_STATUS -> if (resultCode == RESULT_CANCELED) mLocationHelper!!.isGPSEnabled else if (resultCode == RESULT_OK) startLocationUpdate()
            else -> {
            }
        }
    }
}