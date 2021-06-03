package com.app.otmjobs.authentication.ui.activity


import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.CountryResponse
import com.app.otmjobs.authentication.data.model.SignUpRequest
import com.app.otmjobs.authentication.ui.viewmodel.AuthenticationViewModel
import com.app.otmjobs.common.callback.LocationUpdateCallBack
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.callback.SelectPostCodeListener
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.data.model.PostCodeInfo
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.fragment.CountryFlagDialog
import com.app.otmjobs.common.ui.fragment.SearchPostCodeDialog
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.LocationHelper
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.ActivitySignup2Binding
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import com.app.utilities.utils.ValidationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class SignUp2Activity : BaseActivity(), View.OnClickListener, SelectPostCodeListener,
    LocationUpdateCallBack, SelectItemListener {
    private lateinit var binding: ActivitySignup2Binding
    private lateinit var mContext: Context;
    lateinit var signUpRequest: SignUpRequest
    lateinit var searchPostCodeDialog: SearchPostCodeDialog
    private lateinit var mLocationHelper: LocationHelper
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private var isValidPhone = false
    private lateinit var countryResponse: CountryResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_2)
        setStatusBarColor()
        setupToolbar("", true)
        mContext = this
        setPhoneExtensionObservers()
        setCountriesObservers()
        setSignUpObservers()

        mLocationHelper = LocationHelper(this)
        mLocationHelper.setLocationUpdateListener(this)
        if(!mLocationHelper.isGPSEnabled)
            authenticationViewModel.getPhoneExtensionResponse(AppConstants.defaultCountryCode)
        startLocationUpdate()

        binding.txtNext.setOnClickListener(this)
        binding.edtPostCode.setOnClickListener(this)
        binding.txtSearch.setOnClickListener(this)
        binding.routExtensionView.setOnClickListener(this)
        binding.edtCountry.setOnClickListener(this)

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
            signUpRequest =
                Parcels.unwrap(intent.getParcelableExtra(AppConstants.IntentKey.SIGN_UP_REQUEST_DATA))
            binding.signUpRequest = signUpRequest
            getCountries()
        }
    }

    private fun getCountries() {
        showProgressDialog(mContext, "")
        authenticationViewModel.getCountriesResponse()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtNext -> {
                if (valid()) {
                    signUpRequest.device_type = AppConstants.DEVICE_TYPE.toString()
                    signUpRequest.device_token = AppUtils.getDeviceToken()
                    showProgressDialog(mContext, "")
                    authenticationViewModel.register(signUpRequest)
                }
            }
            R.id.edtPostCode, R.id.txtSearch ->
                showPostCodeDialog()
            R.id.routExtensionView, R.id.edtCountry ->
                showFlagListDialog(countryResponse.data)
        }
    }

    private fun valid(): Boolean {
        var valid = true

        if (ValidationUtil.isEmptyEditText(signUpRequest.pin_code)) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtPostCode,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtPostCode.error = null
        }

        if (ValidationUtil.isEmptyEditText(signUpRequest.street_address)) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtStreet,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtStreet.error = null
        }

        if (ValidationUtil.isEmptyEditText(signUpRequest.city)) {
            ValidationUtil.setErrorIntoEditText(
                binding.edtCity,
                mContext.getString(R.string.error_required_field)
            )
            valid = false
            return valid
        } else {
            binding.edtCity.error = null
        }

        if (signUpRequest.country_id == 0) {
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
                StringHelper.removeZeroOnFirstLetter(signUpRequest.phone_number)
            )
        ) {
            if (!ValidationUtil.isValidPhoneNumberRange(
                    StringHelper.removeZeroOnFirstLetter(
                        signUpRequest.phone_number
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
                if (ValidationUtil.isPhoneNumberStartWithZero(signUpRequest.phone_number)) {
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

        return valid
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
                            signUpRequest.country_id = response.country_id
                            signUpRequest.phone_extension_id = response.phone_extension_id
                            binding.edtPhoneExtension.setText(response.phone_extension_name)
                            binding.edtCountry.setText(response.country)
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
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun setSignUpObservers() {
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
                        moveActivity(mContext, DashBoardActivity::class.java, true, true, null)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

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

    private fun showPostCodeDialog() {
        val fm = (mContext as BaseActivity).supportFragmentManager
        searchPostCodeDialog = SearchPostCodeDialog(
            this@SignUp2Activity,
            this,
            AppConstants.DialogIdentifier.SELECT_POST_CODE
        )
        searchPostCodeDialog.show(fm, "dropdownFragment")
    }

    private fun showFlagListDialog(list: MutableList<ModuleInfo>) {
        val fm = supportFragmentManager
        val countryFlagDialog = CountryFlagDialog(
            this@SignUp2Activity,
            list,
            this,
            AppConstants.DialogIdentifier.SELECT_COUNTRY_FLAG
        )
        countryFlagDialog.show(fm, "countryFlagDialog")
    }

    override fun onSelectPostCode(info: PostCodeInfo, tag: Int) {
        binding.edtPostCode.setText(info.postcode)
        binding.edtStreet.setText(info.addressline1 + ", " + info.addressline2 + ", " + info.posttown)
        binding.edtCity.setText(info.county)
        searchPostCodeDialog.dismiss()
    }

    private fun startLocationUpdate() {
        mLocationHelper.startLocationUpdates()
    }

    fun stopLocationUpdate() {
        mLocationHelper.stopLocationUpdates()
    }

    override fun locationUpdate(location: Location) {
        if (signUpRequest.country_id == 0) {
            val address = mLocationHelper.getGeocoderAddress(location.latitude, location.longitude)
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
            signUpRequest.country_id = countryResponse.data[position].id
            signUpRequest.phone_extension_id = countryResponse.data[position].id.toString()
            binding.edtPhoneExtension.setText(countryResponse.data[position].extension)
            binding.edtCountry.setText(countryResponse.data[position].name)
            setCountryFlag(countryResponse.data[position].flag_name)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.IntentKey.LOCATION_SETTING_STATUS -> if (resultCode == RESULT_CANCELED) mLocationHelper.isGPSEnabled else if (resultCode == RESULT_OK) startLocationUpdate()
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdate()
    }
}