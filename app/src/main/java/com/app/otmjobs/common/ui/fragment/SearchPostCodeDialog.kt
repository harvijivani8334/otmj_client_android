package com.app.otmjobs.common.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectPostCodeListener
import com.app.otmjobs.common.data.model.PostCodeInfo
import com.app.otmjobs.common.data.remote.PostCodeInterface
import com.app.otmjobs.common.ui.adapter.PostCodeListAdapter
import com.app.otmjobs.databinding.DialogSearchPostCodeBinding
import com.app.utilities.utils.CollectionUtils
import com.app.utilities.utils.ValidationUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchPostCodeDialog(
    var mContext: Activity,
    var mListener: SelectPostCodeListener,
    var tag: Int
) : DialogFragment() {
    private lateinit var binding: DialogSearchPostCodeBinding
    private lateinit var dialog: AlertDialog
    private lateinit var listPostCode: List<PostCodeInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialogFragmentStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ad = AlertDialog.Builder(mContext)
        val i = mContext.layoutInflater
        val view = i.inflate(R.layout.dialog_search_post_code, null)
        binding = DataBindingUtil.bind(view)!!
        ad.setView(view)
        dialog = ad.create()
        dialog.setCancelable(false)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener { v -> dialog.dismiss() }

        binding.edtSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_SEARCH) {
                if (valid()) {
                    binding.progressBar.visibility = View.VISIBLE
                    hideKeyBoard()
                    callPostCodeAPI()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.imgClearText.setOnClickListener { v ->
            binding.edtSearch.setText("")
            binding.rvPostCode.visibility = View.GONE
        }

        binding.edtSearch.requestFocus()
        showKeyBoard()
        return dialog
    }

    private fun setAdapter() {
        if (CollectionUtils.isNotEmpty(listPostCode)) {
            binding.rvPostCode.visibility = View.VISIBLE
            val adapter = PostCodeListAdapter(
                mContext,
                listPostCode,
                mListener,
                tag
            )
            binding.rvPostCode.layoutManager = LinearLayoutManager(mContext)
            binding.rvPostCode.adapter = adapter
        } else {
            binding.rvPostCode.visibility = View.GONE
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (ValidationUtil.isEmptyEditText(binding.edtSearch.text.toString().trim())) {
            valid = false
        }
        return valid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun showKeyBoard() {
        val imm =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyBoard() {
        val imm =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.routRootView.windowToken, 0)
    }

    fun callPostCodeAPI() {
//        PCW2W-RSPXH-TSKTJ-V3STA
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ws.postcoder.com/pcw/PCWNZ-GXWFW-NMB74-BQ8K5/address/UK/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val postCodeInterface: PostCodeInterface =
            retrofit.create(
                PostCodeInterface::class.java
            )
        val listCall: Call<List<PostCodeInfo>> = postCodeInterface.getPostCodes(
            binding.edtSearch.text.toString().trim(), "json", "3"
        )

        listCall.enqueue(object : Callback<List<PostCodeInfo>> {
            override fun onResponse(
                call: Call<List<PostCodeInfo>>,
                response: Response<List<PostCodeInfo>>
            ) {
                binding.progressBar.visibility = View.GONE
                listPostCode = ArrayList()
                listPostCode = response.body()!!
                setAdapter()
            }

            override fun onFailure(call: Call<List<PostCodeInfo>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}