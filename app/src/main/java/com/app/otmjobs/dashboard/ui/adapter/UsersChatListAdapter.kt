package com.app.otmjobs.dashboard.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.otmjobs.R
import com.app.otmjobs.dashboard.data.model.JobInfo
import com.app.otmjobs.databinding.RowUsersChatListBinding

class UsersChatListAdapter(
    mContext: Context,
    list: ArrayList<JobInfo>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //    private var listAll: ArrayList<JobInfo>
//    private var list: ArrayList<JobInfo>
    var position = 0
    val mContext: Context

    init {
//        listAll = ArrayList<JobInfo>()
//        listAll.addAll(list)
//        this.list = list
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_users_chat_list, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val info: JobInfo = list[position]
        val itemViewHolder = holder as ItemViewHolder
//        itemViewHolder.getData(info)

    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private inner class ItemViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        var binding: RowUsersChatListBinding?
        fun getData(info: JobInfo?) {
//            binding.setInfo(info)
        }

        init {
            binding = DataBindingUtil.bind(itemView!!)
        }
    }

//    fun addProjectInfo(info: ProjectsInfo) {
//        listAll.add(info)
//        list.clear()
//        list.addAll(listAll)
//        notifyDataSetChanged()
//    }


    // Filter Class
    /* fun filter(charText: String) {
         var charText = charText
         val tradeName = ""
         charText = charText.toLowerCase(Locale.getDefault())
         list.clear()
         if (charText.length == 0) {
             list.addAll(listAll)
         } else {
             for (wp in listAll) {
                 try {
                     val name = if (!StringHelper.isEmpty(wp.getName())) wp.getName() else ""
                     if (name.toLowerCase(Locale.getDefault()).contains(charText)) list.add(wp)
                 } catch (e: Exception) {
                     Log.e(javaClass.name, "Exception in Filter :" + e.message)
                 }
             }
         }
         notifyDataSetChanged()
     }*/

//    fun getList(): ArrayList<JobInfo> {
//        return list
//    }
//
//    fun setList(list: ArrayList<JobInfo>) {
//        this.list = list
//    }
}