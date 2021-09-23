package com.app.otmjobs.managejob.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.ActivityJobHistoryBinding
import com.app.otmjobs.managejob.data.model.JobHistoryInfo
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.ui.adapter.JobHistoryListAdapter
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class JobHistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityJobHistoryBinding
    private lateinit var mContext: Context;
    private var adapter: JobHistoryListAdapter? = null
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var postJobRequest: PostJobRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_history)
        setStatusBarColor()
        mContext = this
        setupToolbar(getString(R.string.job_history), true)
        jobHistoryObservers()

        getIntentData()
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            postJobRequest =
                Parcels.unwrap(intent.getParcelableExtra(AppConstants.IntentKey.POST_JOB_DATA))
            binding.txtCategory.text = postJobRequest.trade_name
            showProgressDialog(mContext,"")
            manageJobViewModel.getActionLog(postJobRequest.job_code!!)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.routChatTab -> setupTab(0)
        }
    }

    private fun setAdapter(info: MutableList<JobHistoryInfo>) {
        if (info.size > 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.txtEmptyData.visibility = View.GONE
            binding.recyclerView.setHasFixedSize(true)
            adapter = JobHistoryListAdapter(mContext, info, null)
            binding.recyclerView.adapter = adapter
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.txtEmptyData.visibility = View.VISIBLE
        }
    }

    fun selectCategory(tradeId: Int?, tradeName: String?) {
        val intent = Intent()
        intent.putExtra(AppConstants.IntentKey.CATEGORY_ID, tradeId)
        intent.putExtra(AppConstants.IntentKey.CATEGORY_TITLE, tradeName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun jobHistoryObservers() {
        manageJobViewModel.jobHistoryResponse.observe(this) { response ->
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
                        setAdapter(response.info)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}