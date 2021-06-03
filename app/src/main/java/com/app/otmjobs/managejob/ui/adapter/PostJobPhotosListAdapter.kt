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
import com.app.otmjobs.databinding.RowPostJobPhotosBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import java.io.File

class PostJobPhotosListAdapter(
    var mContext: Context,
    var list: MutableList<JobImageInfo>,
    var listener: SelectItemListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_post_job_photos, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: JobImageInfo = list[position]
        val itemViewHolder = holder as ItemViewHolder

        if (position == 0) {
            itemViewHolder.binding.imgAddImages.visibility = View.VISIBLE
            itemViewHolder.binding.imgGallery.visibility = View.GONE
            itemViewHolder.binding.imgRemoveImage.visibility = View.GONE
        } else {
            itemViewHolder.binding.imgAddImages.visibility = View.GONE
            itemViewHolder.binding.imgGallery.visibility = View.VISIBLE
            itemViewHolder.binding.imgRemoveImage.visibility = View.VISIBLE
            setImage(
                itemViewHolder.binding.imgGallery,
                info.file_name!!,
                info.job_image_id
            )
        }

        itemViewHolder.binding.routMainView.setOnClickListener {
            if (position == 0)
                listener.onSelectItem(position, AppConstants.Action.ADD_PHOTO)
        }

        itemViewHolder.binding.imgRemoveImage.setOnClickListener {
            listener.onSelectItem(position, AppConstants.Action.DELETE_PHOTO)
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
        var binding: RowPostJobPhotosBinding = DataBindingUtil.bind(itemView)!!
    }

    fun setImage(image: ImageView, url: String, id: Int) {
        if (id > 0) {
            GlideUtil.loadImage(url, image, null, null, Constant.ImageScaleType.CENTER_CROP, null)
        } else {
            GlideUtil.loadImageFromFile(
                image,
                File(url),
                AppUtils.getEmptyGalleryDrawable(mContext),
                AppUtils.getEmptyGalleryDrawable(mContext),
                Constant.ImageScaleType.CENTER_CROP,
                null
            )
        }
    }
}