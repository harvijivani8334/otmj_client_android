package com.app.otmjobs.managejob.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.adapter.ViewPagerAdapter
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.PopupMenuHelper
import com.app.otmjobs.dashboard.ui.fragment.UserFragment
import com.app.otmjobs.databinding.ActivityMyJobsDetailsBinding
import com.app.otmjobs.managejob.data.model.AddJobResponse
import com.app.otmjobs.managejob.data.model.DeleteJobRequest
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.ui.fragment.MyJobDetailsFragment
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.callback.DialogButtonClickListener
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class MyJobDetailsActivity : BaseActivity(), View.OnClickListener, SelectItemListener,
    DialogButtonClickListener {
    private lateinit var binding: ActivityMyJobsDetailsBinding
    private lateinit var mContext: Context;
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private var jobId: Int = 0
    private var isUpdated: Boolean = false
    private var selectedStatus: Int = 0
    private var addJobResponse: AddJobResponse = AddJobResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_jobs_details)
        setStatusBarColor()
        mContext = this
        setupToolbar("", true)
        jobDetailsObservers()
        deleteJobObservers()

        binding.imgJob.setOnClickListener {
            if (addJobResponse.info!!.images!!.size > 0) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    AppConstants.IntentKey.POST_JOB_PHOTOS,
                    addJobResponse.info!!.images as ArrayList
                )
                val intent = Intent(mContext, PostJobPagerImagesActivity::class.java)
                intent.putExtras(bundle)
                mContext.startActivity(intent)
            }
        }

        binding.imgMenu.setOnClickListener { v ->
            val list: MutableList<ModuleInfo> = ArrayList()

            val edit = ModuleInfo()
            edit.name = mContext.getString(R.string.edit)

            val delete = ModuleInfo()
            delete.name = mContext.getString(R.string.delete)

            val markAsCompleted = ModuleInfo()
            markAsCompleted.name = mContext.getString(R.string.mark_as_completed)

            val markAsPaused = ModuleInfo()
            markAsPaused.name = mContext.getString(R.string.mark_as_paused)

            val jobHistory = ModuleInfo()
            jobHistory.name = mContext.getString(R.string.job_history)

            when (addJobResponse.info!!.status) {
                AppConstants.JobStatus.Live, AppConstants.JobStatus.Reposted -> {
                    list.add(edit)
                    list.add(delete)
                    list.add(markAsCompleted)
                    list.add(markAsPaused)
                    list.add(jobHistory)
                }
                AppConstants.JobStatus.Paused -> {
                    list.add(edit)
                    list.add(delete)
                    list.add(markAsCompleted)
                    list.add(jobHistory)
                }
                AppConstants.JobStatus.Completed -> {
                    list.add(delete)
                    list.add(jobHistory)
                }
            }

            PopupMenuHelper.showPopupMenu(
                mContext,
                v,
                list,
                addJobResponse.info!!.status!!,
                this
            )
        }

        getIntentData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.routChatTab -> setupTab(0)
        }
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            jobId = intent.getIntExtra(AppConstants.IntentKey.JOB_ID, 0)
            showProgressDialog(mContext, "")
            manageJobViewModel.getJobDetailsResponse(jobId)
        }
    }

    private fun setupViewPager(viewPager: ViewPager, postJobRequest: PostJobRequest) {
        val bundle = Bundle()
        bundle.putParcelable(
            AppConstants.IntentKey.POST_JOB_DATA,
            Parcels.wrap(postJobRequest)
        )
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.addFrag(UserFragment.newInstance(), getString(R.string.trades_person) + " (0)")
        pagerAdapter.addFrag(
            MyJobDetailsFragment.newInstance(bundle),
            getString(R.string.job_details)
        )
        viewPager.adapter = pagerAdapter
        binding.tabs.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 2
    }

    private fun jobDetailsObservers() {
        manageJobViewModel.addJobResponse.observe(this) { response ->
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
                        addJobResponse = response
                        binding.routMainView.visibility = View.VISIBLE
                        binding.info = response.info
                        binding.txtBudget.text = String.format(
                            mContext.getString(R.string.display_budget),
                            AppConstants.CURRENCY,
                            response.info!!.budget,
                        )
                        setImageData(response.info!!.images!!)
                        setupViewPager(binding.viewPager, response.info!!);
                        setStatus(response.info!!.status!!)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun deleteJobObservers() {
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
                        isUpdated = true
                        if (selectedStatus == AppConstants.JobStatus.Deleted) {
                            onBackPressed()
                        } else {
                            addJobResponse.info!!.status = selectedStatus
                            setStatus(addJobResponse.info!!.status!!)
                            selectedStatus = 0;
                        }
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setImageData(list: MutableList<JobImageInfo>) {
        if (list.size > 0) {
            binding.routPhotosCountView.visibility = View.VISIBLE
            binding.txtNoImage.visibility = View.GONE
            binding.txtTotalImage.text = list.size.toString()
            setImage(list[0].file_name!!, binding.imgJob)
        } else {
            binding.routPhotosCountView.visibility = View.GONE
            binding.txtNoImage.visibility = View.VISIBLE
        }
    }

    private fun setImage(imageUrl: String?, imageView: AppCompatImageView) {
        if (!StringHelper.isEmpty(imageUrl)) {
            GlideUtil.loadImage(
                imageUrl,
                imageView,
                null,
                null,
                Constant.ImageScaleType.FIT_CENTER,
                null
            )
        }
    }

    override fun onSelectItem(position: Int, tag: Int) {
        when (tag) {
            AppConstants.JobStatus.Live, AppConstants.JobStatus.Reposted -> {
                when (position) {
                    0 -> onClickEditJob()
                    1 -> onClickDeleteJob()
                    2 -> onClickMarkAsComplete()
                    3 -> onClickMarkAsPaused()
                    4 -> onClickJobHistory()
                }
            }
            AppConstants.JobStatus.Paused -> {
                when (position) {
                    0 -> onClickEditJob()
                    1 -> onClickDeleteJob()
                    2 -> onClickMarkAsComplete()
                    3 -> onClickJobHistory()
                }
            }
            AppConstants.JobStatus.Completed -> {
                when (position) {
                    0 -> onClickDeleteJob()
                    1 -> onClickJobHistory()
                }
            }
        }
    }

    private fun onClickEditJob() {
        val bundle = Bundle()
        bundle.putParcelable(
            AppConstants.IntentKey.POST_JOB_DATA,
            Parcels.wrap(addJobResponse.info!!)
        )
        val intent = Intent(mContext, PostJobDetailsActivity::class.java)
        intent.putExtras(bundle)
        resultEditJob.launch(intent)
    }

    private var resultEditJob =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    private fun onClickDeleteJob() {
        AlertDialogHelper.showDialog(
            mContext,
            "",
            mContext.getString(R.string.error_delete_item),
            mContext.getString(R.string.yes),
            mContext.getString(R.string.no),
            true,
            this,
            AppConstants.DialogIdentifier.DELETE_JOB
        )
    }

    private fun onClickMarkAsComplete() {
        showProgressDialog(mContext, "")
        selectedStatus = AppConstants.JobStatus.Completed
        manageJobViewModel.markAsCompletedResponse(addJobResponse.info!!.job_id!!)
    }

    private fun onClickMarkAsPaused() {
        showProgressDialog(mContext, "")
        selectedStatus = AppConstants.JobStatus.Paused
        manageJobViewModel.markAsPausedResponse(addJobResponse.info!!.job_id!!)
    }

    private fun onClickJobHistory() {
        moveActivity(mContext, JobHistoryActivity::class.java, false, false, null)
    }

    private fun setStatus(status: Int) {
        when (status) {
            AppConstants.JobStatus.Live, AppConstants.JobStatus.Reposted -> {
                binding.txtStatus.text = mContext.getString(R.string.live)
                binding.imgStatus.setColorFilter(mContext.resources.getColor(R.color.green))
            }
            AppConstants.JobStatus.Paused -> {
                binding.txtStatus.text = mContext.getString(R.string.paused)
                binding.imgStatus.setColorFilter(mContext.resources.getColor(R.color.orange))
            }
            AppConstants.JobStatus.Completed -> {
                binding.txtStatus.text = mContext.getString(R.string.completed)
                binding.imgStatus.setColorFilter(mContext.resources.getColor(R.color.colorAccent))
            }
            AppConstants.JobStatus.Deleted -> {
                binding.txtStatus.text = mContext.getString(R.string.deleted)
                binding.imgStatus.setColorFilter(mContext.resources.getColor(R.color.red))
            }
            else -> {
                binding.txtStatus.text = mContext.getString(R.string.live)
                binding.imgStatus.setColorFilter(mContext.resources.getColor(R.color.green))
            }

        }
    }

    override fun onPositiveButtonClicked(dialogIdentifier: Int) {
        if (dialogIdentifier == AppConstants.DialogIdentifier.DELETE_JOB) {
            selectedStatus = AppConstants.JobStatus.Deleted
            val request = DeleteJobRequest()
            request.job_id = addJobResponse.info!!.job_id
            request.device_type = AppConstants.DEVICE_TYPE.toString()
            request.device_token = AppUtils.getDeviceToken()
            showProgressDialog(mContext, "")
            manageJobViewModel.deleteJobResponse(request)
        }
    }

    override fun onNegativeButtonClicked(dialogIdentifier: Int) {

    }

    override fun onBackPressed() {
        if (isUpdated)
            setResult(Activity.RESULT_OK)
        finish()
    }

}