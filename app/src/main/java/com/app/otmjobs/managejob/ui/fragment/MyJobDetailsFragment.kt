package com.app.otmjobs.managejob.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.FragmentMyJobDetailsBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.ui.adapter.DisplayMyJobPhotosListAdapter
import com.app.otmjobs.managejob.ui.adapter.PostJobPhotosListAdapter
import org.parceler.Parcels

class MyJobDetailsFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentMyJobDetailsBinding
    private lateinit var mContext: Context
    private lateinit var postJobRequest: PostJobRequest

    companion object {
        fun newInstance(bundle: Bundle?): MyJobDetailsFragment {
            val fragment = MyJobDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun getBundleData() {
        if (arguments != null) {
            postJobRequest =
                Parcels.unwrap(requireArguments().getParcelable(AppConstants.IntentKey.POST_JOB_DATA))
            binding.info = postJobRequest
            binding.txtBudget.text = String.format(
                mContext.getString(R.string.display_budget),
                AppConstants.CURRENCY,
                postJobRequest.budget,
            )
            setPhotosListAdapter(postJobRequest.images)
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_job_details, container, false)
        mContext = requireActivity()
        getBundleData()
        return binding.root
    }


    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun setPhotosListAdapter(imageList: MutableList<JobImageInfo>?) {
        if (imageList!!.size > 0) {
            binding.rvPhotos.visibility = View.VISIBLE
            binding.txtPhotos.visibility = View.VISIBLE
            binding.rvPhotos.setHasFixedSize(true)
            val adapterPhotos = DisplayMyJobPhotosListAdapter(mContext, imageList!!)
            binding.rvPhotos.adapter = adapterPhotos
        } else {
            binding.rvPhotos.visibility = View.GONE
            binding.txtPhotos.visibility = View.GONE
        }

    }
}