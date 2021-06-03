package com.app.otmjobs.common.ui.adapter

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
import com.app.otmjobs.databinding.RowGallaryItemBinding
import java.io.File

class GalleryImageListAdapter(
    var mContext: Context,
    var list: MutableList<FileDetail>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_gallary_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: FileDetail = list[position]
        val itemViewHolder = holder as ItemViewHolder

        itemViewHolder.binding.imgGallery.visibility = View.VISIBLE
        itemViewHolder.binding.imgCheck.setImageResource(R.drawable.ic_check_circle_green_24dp)
        itemViewHolder.binding.imgCamera.visibility = View.GONE

        if (info.isTick)
            itemViewHolder.binding.imgCheck.visibility = View.VISIBLE
        else
            itemViewHolder.binding.imgCheck.visibility = View.INVISIBLE

        setImage(itemViewHolder.binding.imgGallery, File(info.filePath!!))

        itemViewHolder.binding.routMainView.setOnClickListener {
            if (info.isTick) {
                itemViewHolder.binding.imgCheck.visibility = View.INVISIBLE
                info.isTick = false
                notifyDataSetChanged()
            } else {
                itemViewHolder.binding.imgCheck.setImageResource(R.drawable.ic_check_circle_green_24dp)
                itemViewHolder.binding.imgCheck.visibility = View.VISIBLE
                info.isTick = true
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
        var binding: RowGallaryItemBinding = DataBindingUtil.bind(itemView)!!
    }

    private fun setImage(image: ImageView, file: File?) {
        GlideUtil.loadImageFromFile(
            image,
            file,
            AppUtils.getEmptyGalleryDrawable(mContext),
            AppUtils.getEmptyGalleryDrawable(mContext),
            Constant.ImageScaleType.CENTER_CROP,
            null
        )
    }
}