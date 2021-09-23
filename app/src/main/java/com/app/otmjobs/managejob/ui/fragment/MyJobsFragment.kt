package com.app.otmjobs.managejob.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.activity.SettingsActivity
import com.app.otmjobs.databinding.FragmentMyJobsBinding
import com.app.otmjobs.managejob.data.model.DeleteJobRequest
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.ui.activity.JobHistoryActivity
import com.app.otmjobs.managejob.ui.activity.MyJobDetailsActivity
import com.app.otmjobs.managejob.ui.activity.PostJobDetailsActivity
import com.app.otmjobs.managejob.ui.adapter.JobHistoryListAdapter
import com.app.otmjobs.managejob.ui.adapter.MyJobsListAdapter
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.callback.DialogButtonClickListener
import com.app.utilities.utils.AlertDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class MyJobsFragment : BaseFragment(), View.OnClickListener, SelectItemListener,
    DialogButtonClickListener {
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var binding: FragmentMyJobsBinding
    private lateinit var mContext: Context
    private lateinit var searchView: SearchView
    private lateinit var searchViewItem: MenuItem
    private var adapter: MyJobsListAdapter? = null
    private var selectedJob: Int? = 0
    private var selectedStatus: Int? = 0

    companion object {
        fun newInstance(): MyJobsFragment {
            return MyJobsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_jobs, container, false)
        mContext = requireActivity()
        getMyJobsObservers()
        deleteJobObservers()

        binding.swipeRefreshLayout.setOnRefreshListener { loadData(false) }

        loadData(true)
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    fun loadData(isProgress: Boolean) {
        if (isProgress)
            showProgressDialog(mContext, "")
        manageJobViewModel.getMyJobsResponse()
    }

    private fun setMyJobsListAdapter(list: MutableList<PostJobRequest>?) {
        if (list != null) {
            binding.rvMyJobsList.visibility = View.VISIBLE
            binding.rvMyJobsList.setHasFixedSize(true)
            adapter = MyJobsListAdapter(mContext, list, this)
            binding.rvMyJobsList.adapter = adapter
        } else {
            binding.rvMyJobsList.visibility = View.GONE
        }
    }

    private fun getMyJobsObservers() {
        manageJobViewModel.myJobsResponse.observe(requireActivity()) { response ->
            hideProgressDialog()
            binding.swipeRefreshLayout.isRefreshing = false
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(
                        mContext, null,
                        mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                        null, false, null, 0
                    )
                } else {
                    if (response.IsSuccess) {
                        setMyJobsListAdapter(response.info)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun deleteJobObservers() {
        manageJobViewModel.baseResponse.observe(requireActivity()) { response ->
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
                        if (selectedStatus != 0) {
                            adapter?.list!![selectedJob!!].status = selectedStatus
                            adapter?.notifyDataSetChanged()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.my_jobs_menu, menu)
        searchViewItem = menu.findItem(R.id.action_search)
        searchView = searchViewItem.actionView as SearchView
        searchView.queryHint = getString(R.string.lbl_search)
        searchView.isFocusable = true
        searchView.isIconified = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter?.filter(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSelectItem(position: Int, action: Int) {
        selectedJob = position
        when (action) {
            AppConstants.Action.VIEW_JOB ->
                viewJobDetails(position)
            AppConstants.Action.EDIT_JOB ->
                editJob(position)
            AppConstants.Action.DELETE_JOB -> {
                deleteJob()
            }
            AppConstants.Action.MARK_AS_COMPLETED_JOB -> {
                showProgressDialog(mContext, "")
                selectedStatus = AppConstants.JobStatus.Completed
                manageJobViewModel.markAsCompletedResponse(adapter!!.list[selectedJob!!].job_id!!)
            }
            AppConstants.Action.MARK_AS_PAUSED_JOB -> {
                showProgressDialog(mContext, "")
                selectedStatus = AppConstants.JobStatus.Paused
                manageJobViewModel.markAsPausedResponse(adapter!!.list[selectedJob!!].job_id!!)
            }
            AppConstants.Action.JOB_HISTORY -> {
                val bundle = Bundle()
                bundle.putParcelable(
                    AppConstants.IntentKey.POST_JOB_DATA,
                    Parcels.wrap(adapter!!.list[selectedJob!!])
                )
                moveActivity(mContext, JobHistoryActivity::class.java, false, false, bundle)
            }
        }
    }

    private fun viewJobDetails(position: Int) {
        val bundle = Bundle()
        bundle.putInt(
            AppConstants.IntentKey.JOB_ID, adapter!!.list[position].job_id!!
        )
        val intent = Intent(requireActivity(), MyJobDetailsActivity::class.java)
        intent.putExtras(bundle)
        resultViewJob.launch(intent)
    }

    private var resultViewJob =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadData(true)
            }
        }

    fun editJob(position: Int) {
        val bundle = Bundle()
        bundle.putParcelable(
            AppConstants.IntentKey.POST_JOB_DATA,
            Parcels.wrap(adapter!!.list[position])
        )
        val intent = Intent(requireActivity(), PostJobDetailsActivity::class.java)
        intent.putExtras(bundle)
        resultAddJob.launch(intent)
    }

    fun deleteJob() {
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

    override fun onPositiveButtonClicked(dialogIdentifier: Int) {
        if (dialogIdentifier == AppConstants.DialogIdentifier.DELETE_JOB) {
            val request = DeleteJobRequest()
            request.job_id = adapter!!.list[selectedJob!!].job_id
            request.device_type = AppConstants.DEVICE_TYPE.toString()
            request.device_token = AppUtils.getDeviceToken()
            manageJobViewModel.deleteJobResponse(request)

            adapter!!.list.removeAt(selectedJob!!)
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onNegativeButtonClicked(dialogIdentifier: Int) {

    }

    private var resultAddJob =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadData(true)
            }
        }
}