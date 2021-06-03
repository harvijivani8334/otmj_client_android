package com.app.otmjobs.managejob.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.adapter.ViewPagerAdapter
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.FragmentHomeBinding
import com.app.otmjobs.managejob.data.model.GetTradesResponse
import com.app.otmjobs.managejob.data.model.TradeInfo
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mContext: Context
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var tradesData: GetTradesResponse
    private var all: MutableList<TradeInfo> = ArrayList()
    private var emergency: MutableList<TradeInfo> = ArrayList()
    private var popular: MutableList<TradeInfo> = ArrayList()

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mContext = requireActivity()
        getTradesObservers()
        loadTradesDataAPI()
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun loadTradesDataAPI() {
        showProgressDialog(mContext, "")
        manageJobViewModel.getTradesResponse()
    }

    private fun getTradesObservers() {
        manageJobViewModel.getTradesResponse.observe(requireActivity()) { response ->
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
                        tradesData = response
                        for (i in tradesData.info.indices) {
                            val list: List<String> = tradesData.info[i].type!!.split(",").toList()
                            if (list.contains(AppConstants.Type.JOB_TYPE_REGULAR))
                                all.add(tradesData.info[i])
                            if (list.contains(AppConstants.Type.JOB_TYPE_EMERGENCY))
                                emergency.add(tradesData.info[i])
                            if (list.contains(AppConstants.Type.JOB_TYPE_POPULAR))
                                popular.add(tradesData.info[i])
                        }
                        setupViewPager(binding.viewPager)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        pagerAdapter = ViewPagerAdapter(childFragmentManager)
        //
        var bundle: Bundle = Bundle()

        bundle.putParcelableArrayList(
            AppConstants.IntentKey.CATEGORY_DATA,
            all as ArrayList
        )
        pagerAdapter.addFrag(PostJobListFragment.newInstance(bundle), getString(R.string.all_jobs))

        bundle = Bundle()
        bundle.putParcelableArrayList(
            AppConstants.IntentKey.CATEGORY_DATA,
            emergency as ArrayList
        )
        pagerAdapter.addFrag(
            PostJobListFragment.newInstance(bundle),
            getString(R.string.emergency_jobs)
        )

        bundle = Bundle()
        bundle.putParcelableArrayList(
            AppConstants.IntentKey.CATEGORY_DATA,
            popular as ArrayList
        )
        pagerAdapter.addFrag(
            PostJobListFragment.newInstance(bundle),
            getString(R.string.popular_jobs)
        )

        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 3
        binding.tabs.setupWithViewPager(viewPager)
    }

}