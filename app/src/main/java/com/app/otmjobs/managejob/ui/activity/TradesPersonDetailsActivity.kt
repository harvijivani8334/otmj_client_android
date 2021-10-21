package com.app.otmjobs.managejob.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.MyApplication
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.WorkAroundMapFragment
import com.app.otmjobs.databinding.ActivityTradePersonDetailsBinding
import com.app.otmjobs.managechat.data.model.ChannelInfo
import com.app.otmjobs.managechat.ui.activity.ChatActivity
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.WorkDetailsResponse
import com.app.otmjobs.managejob.data.model.WorkingTimeInfo
import com.app.otmjobs.managejob.ui.adapter.WorkingTimeListAdapter
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import java.util.*

class TradesPersonDetailsActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback {
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var binding: ActivityTradePersonDetailsBinding
    private lateinit var mContext: Context;
    private var userId: String = "";
    private var jobApplicationId: Int = 0;
    private var jobId: String = "";
    private var workDetailsResponse: WorkDetailsResponse = WorkDetailsResponse()
    private val latLngList: MutableList<LatLng> = ArrayList()
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trade_person_details)
        setStatusBarColor()
        mContext = this
        setupToolbar("", true)
        getWorkerDetailsObservers()
        getAcceptRejectJobApplicationObservers()

        (supportFragmentManager.findFragmentById(R.id.map) as WorkAroundMapFragment).setListener(
            object : WorkAroundMapFragment.OnTouchListener {
                override fun onTouch() {
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                }
            })

        val mapFragment = supportFragmentManager.findFragmentById((R.id.map)) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.routEmailView.setOnClickListener(this)
        binding.imgCall.setOnClickListener(this)
        binding.imgMessage.setOnClickListener(this)
        binding.txtAccept.setOnClickListener(this)
        binding.txtReject.setOnClickListener(this)
        binding.imgUserProfile.setOnClickListener(this)
        binding.routChatView.setOnClickListener(this)

        getIntentData()
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            userId = intent.getStringExtra(AppConstants.IntentKey.USER_ID)!!
            jobApplicationId = intent.getIntExtra(AppConstants.IntentKey.JOB_APPLICATION_ID, 0)
            jobId = intent.getStringExtra(AppConstants.IntentKey.JOB_ID)!!
            showProgressDialog(mContext, "")
            manageJobViewModel.getWorkerDetailsResponse(userId, jobId)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.routEmailView -> {
                if (!StringHelper.isEmpty(workDetailsResponse.info!!.email)) {
                    val emailIntent = Intent(
                        Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", workDetailsResponse.info!!.email, null
                        )
                    )
                    startActivity(Intent.createChooser(emailIntent, null))
                }
            }
            R.id.imgCall -> {
                if (!StringHelper.isEmpty(workDetailsResponse.info!!.phone)) {
                    val phoneNumber =
                        "tel:" + workDetailsResponse.info!!.extension + " " + workDetailsResponse.info!!.phone
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(phoneNumber)
                    startActivity(intent)
                }
            }
            R.id.imgMessage -> {
                try {
                    val intent = Intent("android.intent.action.VIEW")
                    val uri =
                        Uri.parse("sms:" + workDetailsResponse.info!!.extension + " " + workDetailsResponse.info!!.phone)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.txtAccept -> {
                showProgressDialog(mContext, "")
                manageJobViewModel.acceptRejectJobApplicationResponse(jobApplicationId, 3)
            }
            R.id.txtReject -> {
                showProgressDialog(mContext, "")
                manageJobViewModel.acceptRejectJobApplicationResponse(jobApplicationId, 2)
            }
            R.id.imgUserProfile -> {
                showUserPhotos()
            }
            R.id.routChatView -> {
                startUserChat(workDetailsResponse.info!!.job_application_chat_id)
            }
        }
    }

    private fun setWorkTimeListAdapter(list: MutableList<WorkingTimeInfo>) {
        val adapter = WorkingTimeListAdapter(mContext, list)
        binding.rvWorkTime.adapter = adapter
    }


    private fun getWorkerDetailsObservers() {
        manageJobViewModel.workDetailsResponse.observe(this) { response ->
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
                        binding.scrollView.visibility = View.VISIBLE
                        if (response.info!!.job_application_status == AppConstants.JobStatus.APPLIED) {
                            binding.routBottomView.visibility = View.VISIBLE
                            binding.txtJobStatus.visibility = View.GONE
                            binding.routChatView.visibility = View.GONE
                        } else {
                            binding.routBottomView.visibility = View.GONE
                            if (response.info!!.job_application_status == AppConstants.JobStatus.REJECTED) {
                                binding.txtJobStatus.visibility = View.VISIBLE
                                binding.txtJobStatus.text = mContext.getString(R.string.rejected)
                                binding.txtJobStatus.setTextColor(mContext.resources.getColor(R.color.red))
                                binding.routChatView.visibility = View.GONE
                            } else if (response.info!!.job_application_status == AppConstants.JobStatus.ACCEPTED) {
                                binding.txtJobStatus.visibility = View.VISIBLE
                                binding.txtJobStatus.text = mContext.getString(R.string.accepted)
                                binding.txtJobStatus.setTextColor(mContext.resources.getColor(R.color.colorAccent))
                                binding.routChatView.visibility = View.VISIBLE
                            }
                        }
                        workDetailsResponse = response
                        binding.info = response.info
                        AppUtils.setUserImage(
                            mContext,
                            response.info!!.image,
                            binding.imgUserProfile
                        )
                        setWorkTimeListAdapter(response.info!!.working_time)
                        if (response.info!!.available_after_work == 0)
                            binding.txtAvailableAfterWork.text = getString(R.string.no)
                        else
                            binding.txtAvailableAfterWork.text = getString(R.string.yes)

                        for (i in response.info!!.working_area.indices) {
                            latLngList.add(
                                LatLng(
                                    response.info!!.working_area[i].lat,
                                    response.info!!.working_area[i].lng
                                )
                            )
                        }
                        setWorkingArea()
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun getAcceptRejectJobApplicationObservers() {
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
                        finish()
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun setWorkingArea() {
        if (latLngList.size > 0) {
            val polygonOptions = PolygonOptions().addAll(latLngList).clickable(false)
            val polygon: Polygon = mMap!!.addPolygon(polygonOptions)
            polygon.strokeColor = resources.getColor(R.color.colorAccent)
            polygon.fillColor = resources.getColor(R.color.colorAccentTransparent)
            polygon.strokeJointType = JointType.ROUND
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList[0], 1.0f))
//            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLngList[0]))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            when (MyApplication().preferenceGetInteger(AppConstants.SharedPrefKey.THEME_MODE, 0)) {
                0 -> googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        mContext,
                        R.raw.style_map_json
                    )
                )
                1 -> googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        mContext,
                        R.raw.dark_mode_style_map_json
                    )
                )
            }
        } catch (e: NotFoundException) {
        }
        mMap!!.uiSettings.isMapToolbarEnabled = false
        mMap!!.uiSettings.isRotateGesturesEnabled = false
    }

    private fun showUserPhotos() {
        if (!StringHelper.isEmpty(workDetailsResponse.info!!.image)) {
            val images: MutableList<JobImageInfo> = ArrayList()
            val jonInfo = JobImageInfo()
            jonInfo.file_name = workDetailsResponse.info!!.image
            images.add(jonInfo)
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                AppConstants.IntentKey.POST_JOB_PHOTOS,
                images as ArrayList
            )
            val intent = Intent(mContext, PostJobPagerImagesActivity::class.java)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
        }
    }

    fun startUserChat(roomId: String?) {
        if (roomId != null) {
            val db = FirebaseFirestore.getInstance()
            val docIdRef = db.collection(AppConstants.FCM_ROOM).document(roomId)
            docIdRef.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        moveToChatRoom(roomId)
                    }
                }
            }
        }

    }

    private fun moveToChatRoom(roomId: String?) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(AppConstants.FCM_ROOM).document(
            roomId!!
        )
        docRef.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val document = task.result
                val info: ChannelInfo? = document.toObject(ChannelInfo::class.java)
                if (info != null) {
                    val bundle = Bundle()
                    info.roomId = roomId
                    bundle.putParcelable(AppConstants.IntentKey.CHANNEL_INFO, Parcels.wrap(info))
                    moveActivity(mContext, ChatActivity::class.java, false, false, bundle)
                }
            }
        }
    }
}