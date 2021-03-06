package com.app.otmjobs.managejob.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.PopupMenuHelper
import com.app.otmjobs.databinding.RowMyJobsBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.data.model.PostJobRequest
import com.app.otmjobs.managejob.ui.activity.PostJobContactInformationActivity
import com.app.otmjobs.managejob.ui.activity.PostJobPagerImagesActivity
import com.app.utilities.utils.StringHelper
import org.parceler.Parcels
import java.util.*
import kotlin.collections.ArrayList

class MyJobsListAdapter(
    var mContext: Context,
    var list: MutableList<PostJobRequest>,
    var listener: SelectItemListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SelectItemListener {
    var selectedPosition = 0
    var listAll: MutableList<PostJobRequest> = ArrayList()

    init {
        this.listAll.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_my_jobs, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: PostJobRequest = list[position]
        val itemViewHolder = holder as ItemViewHolder
        itemViewHolder.getData(info)
        setImageData(info.images!!, itemViewHolder.binding)
        itemViewHolder.binding.txtBudget.text = String.format(
            mContext.getString(R.string.display_budget),
            AppConstants.CURRENCY,
            info.budget,
        )

        itemViewHolder.binding.routMainView.setOnClickListener {
            listener.onSelectItem(position, AppConstants.Action.VIEW_JOB)
        }

        itemViewHolder.binding.imgMenu.setOnClickListener { v ->
            selectedPosition = position
            val list: MutableList<ModuleInfo> = ArrayList()

            val edit = ModuleInfo()
            edit.name = mContext.getString(R.string.edit)
            list.add(edit)

            val delete = ModuleInfo()
            delete.name = mContext.getString(R.string.delete)
            list.add(delete)

            PopupMenuHelper.showPopupMenu(
                mContext,
                v,
                list,
                position,
                this
            )
        }

        itemViewHolder.binding.imgJob.setOnClickListener {
            if (info.images!!.size > 0) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    AppConstants.IntentKey.POST_JOB_PHOTOS,
                    info.images as ArrayList
                )
                val intent = Intent(mContext, PostJobPagerImagesActivity::class.java)
                intent.putExtras(bundle)
                mContext.startActivity(intent)
            }

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
        var binding: RowMyJobsBinding = DataBindingUtil.bind(itemView)!!
        fun getData(info: PostJobRequest?) {
            binding.info = info
        }

    }

    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        list.clear()
        if (charText.isEmpty()) {
            list.addAll(listAll)
        } else {
            for (wp in listAll) {
                try {
                    val name = if (!StringHelper.isEmpty(wp.trade_name)) wp.trade_name else ""
                    val description =
                        if (!StringHelper.isEmpty(wp.job_description)) wp.job_description else ""
                    if (name!!.toLowerCase(Locale.getDefault()).contains(charText)
                        || description!!.toLowerCase(Locale.getDefault()).contains(charText)
                    )
                        list.add(wp)
                } catch (e: Exception) {
                    Log.e(javaClass.name, "Exception in Filter :" + e.message)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onSelectItem(position: Int, tag: Int) {
        when (position) {
            0 -> listener.onSelectItem(tag, AppConstants.Action.EDIT_JOB)
            1 -> listener.onSelectItem(tag, AppConstants.Action.DELETE_JOB)
        }
    }

    private fun setImageData(list: MutableList<JobImageInfo>, binding: RowMyJobsBinding) {
        if (list.size > 0) {
            binding.routPhotosCountView.visibility = View.VISIBLE
            binding.txtNoImage.visibility = View.GONE
            binding.txtTotalImage.text = list.size.toString()
            setImage(list[0].file_name!!, binding.imgJob)
        } else {
            binding.routPhotosCountView.visibility = View.GONE
            binding.txtNoImage.visibility = View.VISIBLE
        }
    }

    private fun setImage(imageUrl: String?, imageView: AppCompatImageView) {
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
}