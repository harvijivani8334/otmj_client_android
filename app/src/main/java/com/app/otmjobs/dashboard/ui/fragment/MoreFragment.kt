package com.app.otmjobs.dashboard.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.MyApplication
import com.app.otmjobs.R
import com.app.otmjobs.authentication.ui.activity.IntroductionActivity
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.dashboard.ui.activity.AppInformationActivity
import com.app.otmjobs.dashboard.ui.activity.SettingsActivity
import com.app.otmjobs.databinding.FragmentMoreBinding
import com.app.utilities.callback.DialogButtonClickListener
import com.app.utilities.utils.AlertDialogHelper

class MoreFragment : BaseFragment(), View.OnClickListener, DialogButtonClickListener {
    private lateinit var binding: FragmentMoreBinding
    private lateinit var mContext: Context

    companion object {
        fun newInstance(): MoreFragment {
            return MoreFragment()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        mContext = requireActivity()
        binding.routAppInformation.setOnClickListener(this)
        binding.routSettings.setOnClickListener(this)
        binding.routLogout.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.routSettings ->
                moveActivity(mContext, SettingsActivity::class.java, false, false, null)
            R.id.routAppInformation ->
                moveActivity(mContext, AppInformationActivity::class.java, false, false, null)
            R.id.routLogout -> {
                AlertDialogHelper.showDialog(
                    mContext,
                    "",
                    getString(R.string.logout_msg),
                    getString(R.string.yes),
                    getString(R.string.no),
                    false,
                    this,
                    AppConstants.DialogIdentifier.LOGOUT
                )

            }
        }
    }

    override fun onPositiveButtonClicked(dialogIdentifier: Int) {
        if (dialogIdentifier == AppConstants.DialogIdentifier.LOGOUT) {
            MyApplication().clearData()
            moveActivity(mContext, IntroductionActivity::class.java, true, true, null)
        }
    }

    override fun onNegativeButtonClicked(dialogIdentifier: Int) {

    }
}