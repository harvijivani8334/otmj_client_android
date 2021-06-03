package com.app.otmjobs.managejob.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.otmjobs.R
import com.app.otmjobs.databinding.RowSelectTradeListBinding
import com.app.otmjobs.managejob.data.model.TradeInfo
import com.app.otmjobs.managejob.ui.activity.SelectTradeActivity
import java.util.*

class SelectTradeListAdapter(
    var mContext: Context,
    var list: MutableList<TradeInfo>,
    var tradeId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var position = 0
    var listOfAllData: MutableList<TradeInfo> = ArrayList()

    init {
        listOfAllData.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_select_trade_list, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: TradeInfo = list[position]
        val itemViewHolder = holder as ItemViewHolder
        itemViewHolder.getData(info)
        if (tradeId == info.trade_id)
            itemViewHolder.binding.imgCheck.visibility = View.VISIBLE
        else
            itemViewHolder.binding.imgCheck.visibility = View.GONE

        itemViewHolder.binding.routMainView.setOnClickListener {
            if (mContext is SelectTradeActivity)
                (mContext as SelectTradeActivity).selectCategory(
                    list[position].trade_id,
                    list[position].trade_name
                )
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
        var binding: RowSelectTradeListBinding = DataBindingUtil.bind(itemView)!!
        fun getData(info: TradeInfo) {
            binding.info = info
        }
    }

    fun filter(charText: String) {
        charText.toLowerCase(Locale.getDefault())
        list.clear()
        if (charText.isEmpty()) {
            list.addAll(listOfAllData)
        } else {
            for (wp in listOfAllData) {
                try {
                    if (java.lang.String.valueOf(wp.trade_name).toLowerCase(Locale.getDefault())
                            .contains(charText)
                    ) {
                        list.add(wp)
                    }
                } catch (e: Exception) {
                    Log.e(javaClass.name, "Exception in Filter :" + e.message)
                }
            }
        }
        notifyDataSetChanged()
    }
}