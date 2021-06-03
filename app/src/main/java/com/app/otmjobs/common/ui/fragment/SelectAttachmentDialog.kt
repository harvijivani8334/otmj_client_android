package com.app.otmjobs.common.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectAttachmentListener
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.DialogSelectAttachmentBinding

class SelectAttachmentDialog(
    var mContext: Activity,
    var mListener: SelectAttachmentListener,
    var tag: Int,
    var isCamera: Boolean,
    var isGallery: Boolean
) : DialogFragment(), View.OnClickListener {
    private lateinit var binding: DialogSelectAttachmentBinding
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialogFragmentStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ad = AlertDialog.Builder(mContext)
        val i = mContext.layoutInflater
        val view = i.inflate(R.layout.dialog_select_attachment, null)
        binding = DataBindingUtil.bind(view)!!
        ad.setView(view)
        dialog = ad.create()

        if (isCamera)
            binding.routCamera.visibility = View.VISIBLE

        if (isGallery)
            binding.routPhotos.visibility = View.VISIBLE

        binding.routCamera.setOnClickListener(this)
        binding.routPhotos.setOnClickListener(this)

        return dialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.routCamera -> mListener.onSelectAttachment(AppConstants.Type.SELECT_FROM_CAMERA)
            R.id.routPhotos -> mListener.onSelectAttachment(AppConstants.Type.SELECT_PHOTOS)
        }
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}