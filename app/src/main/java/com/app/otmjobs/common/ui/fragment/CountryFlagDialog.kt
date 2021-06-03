package com.app.otmjobs.common.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.databinding.DialogCountryFlagBinding
import com.app.otmjobs.databinding.RowContryFlagListBinding
import com.app.utilities.utils.CollectionUtils
import com.app.utilities.utils.StringHelper
import java.util.*
import kotlin.collections.ArrayList

class CountryFlagDialog(
    var mContext: Activity,
    var list: MutableList<ModuleInfo>,
    var selectItemListener: SelectItemListener,
    var identifyId: Int
) : DialogFragment() {

    private lateinit var binding: DialogCountryFlagBinding
    private lateinit var dialog: AlertDialog
    var mList: MutableList<ModuleInfo> = ArrayList()
    private var adapter: ListAdapter? = null

    init {
        for (i in 0 until list.size) {
            list[i].position = i
            mList.add(list[i])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialogFragmentStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ad = AlertDialog.Builder(mContext)
        val i = mContext.layoutInflater
        val view = i.inflate(R.layout.dialog_country_flag, null)
        binding = DataBindingUtil.bind(view)!!
        ad.setView(view)
        dialog = ad.create()
        dialog.setCancelable(false)

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter!!.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        setAdapter()

        return dialog
    }

    private fun setAdapter() {
        if (CollectionUtils.isNotEmpty(mList)) {
            binding.recyclerView.visibility = View.VISIBLE
            adapter = ListAdapter(
                mList,
                selectItemListener,
                identifyId
                , dialog
            )
            binding.recyclerView.layoutManager = LinearLayoutManager(mContext)
            binding.recyclerView.adapter = adapter
        } else {
            binding.recyclerView.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    class ListAdapter(
        private var mList: MutableList<ModuleInfo>,
        var listener: SelectItemListener,
        var identifyId: Int,
        val dialog: AlertDialog
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var listOfAllData: MutableList<ModuleInfo> = ArrayList()

        init {
            listOfAllData.addAll(mList)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_contry_flag_list, parent, false)
            return ItemViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data: ModuleInfo = mList.get(position)
            val itemViewHolder = holder as ItemViewHolder
            itemViewHolder.binding.txtName.setText(
                data.name + " (" + data.extension + ")"
            )
            if (!StringHelper.isEmpty(data.flag_name)) GlideUtil.loadImage(
                data.flag_name,
                itemViewHolder.binding.imgFlag,
                null,
                null,
                Constant.ImageScaleType.FIT_CENTER,
                null
            )
            itemViewHolder.binding.routMainView.setOnClickListener { v ->
                listener.onSelectItem(
                    data.position,
                    identifyId
                )
                dialog.dismiss()
            }
            itemViewHolder.getData(data)
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        private inner class ItemViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var binding: RowContryFlagListBinding = DataBindingUtil.bind(itemView)!!
            fun getData(info: ModuleInfo) {
                binding.info = info
            }
        }

        // Filter Class
        fun filter(charText: String) {
            var charText = charText
            charText = charText.toLowerCase(Locale.getDefault())
            mList.clear()
            if (charText.isEmpty()) {
                mList.addAll(listOfAllData)
            } else {
                for (wp in listOfAllData) {
                    try {
                        if (java.lang.String.valueOf(wp.name).toLowerCase(Locale.getDefault())
                                .contains(charText)
                        ) {
                            mList.add(wp)
                        }
                    } catch (e: Exception) {
                        Log.e(javaClass.name, "Exception in Filter :" + e.message)
                    }
                }
            }
            notifyDataSetChanged()
        }
    }
}