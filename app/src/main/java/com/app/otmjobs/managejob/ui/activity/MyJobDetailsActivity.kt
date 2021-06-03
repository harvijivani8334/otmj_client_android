package com.app.otmjobs.managejob.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.data.model.FileDetail
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.adapter.ViewPagerAdapter
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.fragment.UserFragment
import com.app.otmjobs.databinding.ActivityMyJobsDetailsBinding
import com.app.otmjobs.databinding.RowMyJobsBinding
import com.app.otmjobs.managejob.data.model.AddJobResponse
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.ui.adapter.PostJobListAdapter
import com.app.otmjobs.managejob.ui.fragment.MyJobDetailsFragment
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class MyJobDetailsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMyJobsDetailsBinding
    private lateinit var mContext: Context;
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private var jobId: Int = 0
    private var addJobResponse: AddJobResponse = AddJobResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_jobs_details)
        setStatusBarColor()
        mContext = this
        setupToolbar("", true)
        jobDetailsObservers()

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
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

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
}