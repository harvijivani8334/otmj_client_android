package com.app.otmjobs.managejob.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.FragmentUsersBinding
import com.app.otmjobs.managejob.data.model.TradePersonInfo
import com.app.otmjobs.managejob.ui.activity.MyJobDetailsActivity
import com.app.otmjobs.managejob.ui.activity.TradesPersonDetailsActivity
import com.app.otmjobs.managejob.ui.adapter.TradesPersonListAdapter
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserFragment : BaseFragment(), View.OnClickListener, SelectItemListener {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var mContext: Context
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var adapter: TradesPersonListAdapter
    private var jobId: Int = 0

    companion object {
        fun newInstance(bundle: Bundle?): UserFragment {
            val fragment = UserFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun getBundleData() {
        if (arguments != null) {
            jobId = requireArguments().getInt(AppConstants.IntentKey.JOB_ID)
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        mContext = requireActivity()
        getBundleData()
        getTradePersonObservers()
        showProgressDialog(mContext, "")
        if (jobId == 0)
            manageJobViewModel.getTradesPersonResponse()
        else
            manageJobViewModel.getTradesPersonResponse(jobId)
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun setTradesmanListAdapter(list: MutableList<TradePersonInfo>) {
        if (list.size > 0) {
            binding.rvUsersList.visibility = View.VISIBLE
            binding.tvEmptyText.visibility = View.GONE
            binding.rvUsersList.setHasFixedSize(true)
            adapter = TradesPersonListAdapter(mContext, list, this)
            binding.rvUsersList.adapter = adapter
        } else {
            binding.rvUsersList.visibility = View.GONE
            binding.tvEmptyText.visibility = View.VISIBLE
        }
    }

    private fun getTradePersonObservers() {
        manageJobViewModel.tradesPersonResponse.observe(requireActivity()) { response ->
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
                        setTradesmanListAdapter(response.info)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onSelectItem(position: Int, action: Int) {
        if (action == AppConstants.Action.VIEW_USER) {
            val bundle = Bundle()
            bundle.putString(
                AppConstants.IntentKey.USER_ID, adapter.list[position].worker_id!!
            )
            bundle.putInt(
                AppConstants.IntentKey.JOB_APPLICATION_ID, adapter.list[position].job_application_id
            )
            val intent = Intent(requireActivity(), TradesPersonDetailsActivity::class.java)
            intent.putExtras(bundle)
            resultViewTradePerson.launch(intent)
        }
    }

    private var resultViewTradePerson =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

            }
        }
}