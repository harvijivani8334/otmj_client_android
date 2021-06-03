package com.app.otmjobs.common.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectPostCodeListener
import com.app.otmjobs.common.data.model.PostCodeInfo
import com.app.otmjobs.databinding.RowPostCodeListBinding
import com.app.utilities.utils.StringHelper

class PostCodeListAdapter(
    var mContext: Context,
    var list: List<PostCodeInfo> = ArrayList(),
    var selectPostCodeListener: SelectPostCodeListener,
    var tag:Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_post_code_list, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: PostCodeInfo = list[position]
        val itemViewHolder = holder as ItemViewHolder
        var summery = ""
        if (!StringHelper.isEmpty(info.addressline2)) {
            summery = info.addressline2
        }
        if (!StringHelper.isEmpty(info.addressline3)) {
            summery =
                if (!StringHelper.isEmpty(summery)) summery + ", " + info.addressline3 else info.addressline3
        }
        if (!StringHelper.isEmpty(info.posttown)) {
            summery =
                if (!StringHelper.isEmpty(summery)) summery + ", " + info.posttown else info.posttown
        }
        if (!StringHelper.isEmpty(info.postcode)) {
            summery =
                if (!StringHelper.isEmpty(summery)) summery + ", " + info.postcode else info.postcode
        }
        itemViewHolder.binding.txtSubTitle.setText(summery)
        itemViewHolder.binding.routMainView.setOnClickListener { v ->
            selectPostCodeListener.onSelectPostCode(info,tag)
        }
        itemViewHolder.getData(info)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ) {
        val binding: RowPostCodeListBinding = DataBindingUtil.bind(itemView)!!
        fun getData(info: PostCodeInfo?) {
            binding.setInfo(info)
        }

    }
}
