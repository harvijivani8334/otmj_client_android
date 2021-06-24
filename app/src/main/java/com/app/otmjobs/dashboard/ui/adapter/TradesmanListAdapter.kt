package com.app.otmjobs.dashboard.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.otmjobs.R
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.databinding.RowTradesmanListBinding
import com.app.otmjobs.managejob.data.model.TradePersonInfo

class TradesmanListAdapter(
    var mContext: Context,
    var list: MutableList<TradePersonInfo>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var position = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_tradesman_list, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: TradePersonInfo = list[position]
        val itemViewHolder = holder as ItemViewHolder
        itemViewHolder.getData(info)
        AppUtils.setUserImage(mContext, info.image, itemViewHolder.binding!!.imgUser)
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

    private inner class ItemViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        var binding: RowTradesmanListBinding? = DataBindingUtil.bind(itemView!!)
        fun getData(info: TradePersonInfo) {
            binding!!.info = info
        }
    }
}