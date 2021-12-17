package com.app.otmjobs.managejob.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.RowInvoiceListBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.SaveInvoiceRequest
import com.app.otmjobs.managejob.ui.activity.PostJobPagerImagesActivity
import com.app.utilities.utils.StringHelper

class InvoiceListAdapter(
    var mContext: Context,
    var list: MutableList<SaveInvoiceRequest>,
    var selectItemListener: SelectItemListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var position = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_invoice_list, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: SaveInvoiceRequest = list[position]
        val itemViewHolder = holder as ItemViewHolder

        setImage(info.invoice_image, itemViewHolder.binding.imgCertificate)

        itemViewHolder.binding.imgDownload.setOnClickListener {
            selectItemListener.onSelectItem(position, AppConstants.Action.DOWNLOAD_INVOICE)
        }

        itemViewHolder.binding.imgCertificate.setOnClickListener {
            val images: MutableList<JobImageInfo> = java.util.ArrayList()

            val jonInfo = JobImageInfo()
            jonInfo.file_name = info.invoice_image
            images.add(jonInfo)

            val bundle = Bundle()
            bundle.putParcelableArrayList(
                AppConstants.IntentKey.POST_JOB_PHOTOS,
                images as ArrayList
            )
            val intent = Intent(mContext, PostJobPagerImagesActivity::class.java)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: RowInvoiceListBinding = DataBindingUtil.bind(itemView)!!
    }

    fun setImage(imageUrl: String?, imageView: AppCompatImageView) {
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

    fun addItem(info: SaveInvoiceRequest) {
        list.add(info)
        notifyDataSetChanged()
    }

//    private fun setImageData(list: Array<String>, binding: RowAllJobsBinding) {
//        if (list.isNotEmpty()) {
//            binding.routPhotosCountView.visibility = View.VISIBLE
//            binding.txtNoImage.visibility = View.GONE
//            binding.txtTotalImage.text = list.size.toString()
//            setImage(list[0], binding.imgJob)
//        } else {
//            binding.routPhotosCountView.visibility = View.GONE
//            binding.txtNoImage.visibility = View.VISIBLE
//        }
//    }
}