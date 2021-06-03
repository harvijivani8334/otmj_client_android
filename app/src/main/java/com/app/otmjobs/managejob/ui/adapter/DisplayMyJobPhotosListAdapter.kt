package com.app.otmjobs.managejob.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.data.model.FileDetail
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.RowMyJobDisplayPhotosBinding
import com.app.otmjobs.databinding.RowPostJobPhotosBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import java.io.File

class DisplayMyJobPhotosListAdapter(
    var mContext: Context,
    var list: MutableList<JobImageInfo>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_my_job_display_photos, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: JobImageInfo = list[position]
        val itemViewHolder = holder as ItemViewHolder

        setImage(
            itemViewHolder.binding.imgGallery,
            info.file_name!!,
        )
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
        var binding: RowMyJobDisplayPhotosBinding = DataBindingUtil.bind(itemView)!!
    }

    fun setImage(image: ImageView, url: String) {
        GlideUtil.loadImage(url, image, null, null, Constant.ImageScaleType.CENTER_CROP, null)
    }
}