package com.app.otmjobs.managejob.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.FragmentPostJobListBinding
import com.app.otmjobs.managejob.data.model.GetTradesResponse
import com.app.otmjobs.managejob.data.model.TradeInfo
import com.app.otmjobs.managejob.ui.activity.PostJobDetailsActivity
import com.app.otmjobs.managejob.ui.adapter.PostJobListAdapter
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PostJobListFragment : BaseFragment(), View.OnClickListener, SelectItemListener {
    private lateinit var binding: FragmentPostJobListBinding
    private lateinit var mContext: Context
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var adapter: PostJobListAdapter
    private var listCategories: MutableList<TradeInfo> = ArrayList()

    companion object {
        fun newInstance(bundle: Bundle?): PostJobListFragment {
            val fragment = PostJobListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun getBundleData() {
        if (arguments != null) {
            listCategories.addAll(requireArguments().getParcelableArrayList(AppConstants.IntentKey.CATEGORY_DATA)!!)
            setPostJobListAdapter()
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_job_list, container, false)
        mContext = requireActivity()
        getBundleData()
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun setPostJobListAdapter() {
        if (listCategories.size > 0) {
            binding.rvPostJobList.visibility = View.VISIBLE
            binding.rvPostJobList.setHasFixedSize(true)
            adapter = PostJobListAdapter(mContext, listCategories.toMutableList(), this)
            binding.rvPostJobList.adapter = adapter
        } else {
            binding.rvPostJobList.visibility = View.GONE
        }
    }

    override fun onSelectItem(position: Int, action: Int) {
        if (action == AppConstants.Action.SELECT_CATEGORY) {
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                AppConstants.IntentKey.CATEGORY_DATA,
                listCategories as ArrayList
            )
            bundle.putInt(AppConstants.IntentKey.CATEGORY_ID, listCategories[position].trade_id!!)
            bundle.putString(
                AppConstants.IntentKey.CATEGORY_TITLE,
                listCategories[position].trade_name!!
            )

            val intent = Intent(requireActivity(), PostJobDetailsActivity::class.java)
            intent.putExtras(bundle)
            resultAddJob.launch(intent)
        }
    }

    var resultAddJob =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                (activity as DashBoardActivity?)?.refreshMyJobsFragment()
            }
        }
}