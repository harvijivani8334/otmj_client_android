package com.app.otmjobs.managejob.ui.adapter

import android.content.Context
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
import com.app.otmjobs.databinding.RowJobHistoryListBinding
import com.app.otmjobs.databinding.RowPostJobItemBinding
import com.app.otmjobs.managejob.data.model.TradeInfo
import com.app.utilities.utils.StringHelper

class JobHistoryListAdapter(
    var mContext: Context,
    var list: MutableList<TradeInfo>?,
    var selectItemListener: SelectItemListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var position = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_job_history_list, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val info: TradeInfo = list[position]
//        val itemViewHolder = holder as ItemViewHolder
//        setImage(info.icon, itemViewHolder.binding.imgJob)
//        itemViewHolder.getData(info)
//        itemViewHolder.binding.routMainView.setOnClickListener {
//            selectItemListener.onSelectItem(position, AppConstants.Action.SELECT_CATEGORY)
//        }
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: RowJobHistoryListBinding = DataBindingUtil.bind(itemView)!!
        fun getData(info: TradeInfo) {
//            binding.info = info
        }
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
}