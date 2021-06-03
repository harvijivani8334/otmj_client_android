package com.app.otmjobs.authentication.ui.adapter

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
import com.app.otmjobs.authentication.data.model.User
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.PopupMenuHelper
import com.app.otmjobs.databinding.RowLoginUsersBinding
import com.app.otmjobs.databinding.RowMyJobsBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.ui.activity.PostJobPagerImagesActivity
import com.app.utilities.utils.StringHelper
import java.util.*
import kotlin.collections.ArrayList

class LoginUsersListAdapter(
    var mContext: Context,
    var list: MutableList<User>,
    var listener: SelectItemListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SelectItemListener {
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_login_users, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info: User = list[position]
        val itemViewHolder = holder as ItemViewHolder
        itemViewHolder.getData(info)
        itemViewHolder.binding.txtName.text = info.first_name + " " + info.last_name

        AppUtils.setUserImage(mContext, info.image, itemViewHolder.binding.imgUserPic)

        itemViewHolder.binding.routMainView.setOnClickListener {
            listener.onSelectItem(position, AppConstants.Action.VIEW_LOGIN_USER)
        }

        itemViewHolder.binding.imgMore.setOnClickListener { v ->
            selectedPosition = position
            val list: MutableList<ModuleInfo> = ArrayList()

            val remove = ModuleInfo()
            remove.name = mContext.getString(R.string.remove)
            list.add(remove)
            
            PopupMenuHelper.showPopupMenu(
                mContext,
                v,
                list,
                position,
                this
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
        var binding: RowLoginUsersBinding = DataBindingUtil.bind(itemView)!!
        fun getData(info: User?) {
            binding.info = info
        }
    }

    override fun onSelectItem(position: Int, tag: Int) {
        when (position) {
            0 -> listener.onSelectItem(tag, AppConstants.Action.DELETE_LOGIN_USER)
        }
    }
}