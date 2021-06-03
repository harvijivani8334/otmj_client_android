package com.app.otmjobs.common.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.LocationUpdateCallBack
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class LocationHelper(var mContext: Activity) {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mLocation: Location
    private var isPermission = false
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var listener: LocationUpdateCallBack

    init {
        getLocation()
    }

    private fun getLocation() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
            locationRequest = LocationRequest.create()
            locationRequest.interval = 5000
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        mLocation = location
                        listener.locationUpdate(mLocation)
                    }
                }

                override fun onLocationAvailability(p0: LocationAvailability) {
                    super.onLocationAvailability(p0)
                }
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //for stop location update
    fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    //for start location update
    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
    }

    fun getFullAddress(latitude: Double, longitude: Double): String {
        var currentLocation = ""
        try {
            val geocoder = Geocoder(mContext, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            currentLocation = addresses[0].getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentLocation
    }

    fun getCountryCode(latitude: Double, longitude: Double): String {
        var country = ""
        try {
            val geocoder = Geocoder(mContext, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            country = addresses[0].countryCode
            Log.e("test", "country:$country")
            Log.e("test", "country Code:" + addresses[0].countryCode)
            Log.e("test", "addresss:" + addresses[0].getAddressLine(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return country
    }

    fun getGeocoderAddress(latitude: Double, longitude: Double): Address? {
        var address: Address? = null
        try {
            val geocoder = Geocoder(mContext, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            address = addresses[0]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return address
    }

    val isLocationPermission: Boolean
        get() {
            isPermission = if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                EasyPermissions.requestPermissions(
                    mContext,
                    mContext.getString(R.string.msg_location_permission),
                    AppConstants.IntentKey.RC_LOCATION_PERM,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                false
            } else {
                true
            }
            return isPermission
        }

    val isGPSEnabled: Boolean
        get() {
            var isGpsOn = false
            if (isLocationPermission) {
                val manager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (getLocationMode(mContext) == AppConstants.LocationMode.LOCATION_MODE_HIGH_ACCURACY) {
                        isGpsOn = true
                        startLocationUpdates()
                    } else {
                        showSettingsAlert()
                        isGpsOn = false
                    }
                } else {
                    isGpsOn = false
                    enableLoc(mContext)
                }
            }
            return isGpsOn
        }

    fun enableLoc(context: Activity?) {
        googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(p0: Bundle?) {

                }

                override fun onConnectionSuspended(p0: Int) {
                    googleApiClient.connect()
                }
            })
            .addOnConnectionFailedListener { }.build()
        googleApiClient.connect()
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 1000
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result1 ->
            val status: Status = result1.getStatus()
            when (status.getStatusCode()) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(
                        context,
                        AppConstants.IntentKey.LOCATION_SETTING_STATUS
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getLocationMode(context: Context): Int {
        var locationMode = 0
        try {
            locationMode =
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        return locationMode
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setMessage(mContext.getString(R.string.msg_gps_enable))
        alertDialog.setPositiveButton(
            mContext.getString(R.string.settings)
        ) { dialog: DialogInterface?, which: Int ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        alertDialog.setNegativeButton(
            mContext.getString(R.string.cancel)
        ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        alertDialog.show()
    }

    fun setLocationUpdateListener(callBack: LocationUpdateCallBack) {
        listener = callBack
    }

}