package com.app.otmjobs.managejob.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.otmjobs.R
import com.app.otmjobs.databinding.RowWorkingTimeListBinding
import com.app.otmjobs.managejob.data.model.WorkingTimeInfo

class WorkingTimeListAdapter(
    var mContext: Context,
    var list: MutableList<WorkingTimeInfo>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_working_time_list, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: WorkingTimeInfo = list[position]
        val itemViewHolder = holder as ItemViewHolder

        if (info.status!!) {
            itemViewHolder.binding.txtTitle.visibility = View.VISIBLE
            itemViewHolder.binding.txtTitle.text =
                info.day + " - " + info.start_time + " to " + info.end_time
        } else {
            itemViewHolder.binding.txtTitle.visibility = View.GONE
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
        var binding: RowWorkingTimeListBinding = DataBindingUtil.bind(itemView)!!

    }
}