package com.app.otmjobs.managejob.ui.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.DownloadFileListener
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.FileUtils
import com.app.otmjobs.databinding.FragmentJobInvoiceBinding
import com.app.otmjobs.managejob.data.model.SaveInvoiceRequest
import com.app.otmjobs.managejob.ui.adapter.InvoiceListAdapter
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import com.app.utilities.utils.AlertDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class JobInvoiceFragment : BaseFragment(), View.OnClickListener, SelectItemListener,
    DownloadFileListener, EasyPermissions.PermissionCallbacks {
    private val manageJobViewModel: ManageJobViewModel by viewModel()
    private lateinit var binding: FragmentJobInvoiceBinding
    private lateinit var mContext: Context
    private lateinit var adapter: InvoiceListAdapter
    private var selectedInvoice: Int = 0
    private var jobId: Int = 0

    companion object {
        fun newInstance(bundle: Bundle?): JobInvoiceFragment {
            val fragment = JobInvoiceFragment()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_job_invoice, container, false)
        mContext = requireActivity()
        getBundleData()
        getInvoicesObservers()

        binding.swipeRefreshLayout.setOnRefreshListener { loadData(false) }
        loadData(true)

        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun loadData(isProgress: Boolean) {
        if (isProgress)
            showProgressDialog(mContext, "")
        manageJobViewModel.getInvoices(jobId.toString())
    }

    private fun setAdapter(list: MutableList<SaveInvoiceRequest>) {
        if (list.size > 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.txtEmptyData.visibility = View.GONE
            binding.recyclerView.setHasFixedSize(true)
            adapter = InvoiceListAdapter(mContext, list, this)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.txtEmptyData.visibility = View.VISIBLE
        }

    }

    private fun getInvoicesObservers() {
        manageJobViewModel.getInvoicesResponse.observe(requireActivity()) { response ->
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
                        setAdapter(response.info)
                    } else {
                        AppUtils.handleUnauthorized(mContext, response)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onSelectItem(position: Int, action: Int) {
        when (action) {
            AppConstants.Action.DOWNLOAD_INVOICE -> {
                selectedInvoice = position
                checkPermission()
            }
        }
    }

    override fun onFileDownloaded(directory: String, extension: String, action: Int) {
        Log.e("test", "onFileDownloaded");
        FileUtils.viewPdf(mContext, directory)
    }

    private fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun checkPermission() {
        if (hasPermission()) {
            FileUtils.download(mContext, adapter.list[selectedInvoice].invoice_pdf, this)
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.msg_storage_permission),
                AppConstants.IntentKey.EXTERNAL_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        FileUtils.download(mContext, adapter.list[selectedInvoice].invoice_pdf, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }
}