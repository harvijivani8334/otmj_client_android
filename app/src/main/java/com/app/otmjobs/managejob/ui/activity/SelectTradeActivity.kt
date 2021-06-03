package com.app.otmjobs.managejob.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.ActivitySelectTradeBinding
import com.app.otmjobs.managejob.data.model.TradeInfo
import com.app.otmjobs.managejob.ui.adapter.SelectTradeListAdapter

class SelectTradeActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySelectTradeBinding
    private lateinit var mContext: Context;
    private var adapter: SelectTradeListAdapter? = null
    private var listCategories: MutableList<TradeInfo> = ArrayList()
    private var tradeId: Int = 0
    private lateinit var searchView: SearchView
    private lateinit var searchViewItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_trade)
        setStatusBarColor()
        mContext = this
        setupToolbar(getString(R.string.select_category), true)
        getIntentData()
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            tradeId = intent.getIntExtra(AppConstants.IntentKey.CATEGORY_ID, 0);
            listCategories.addAll(intent.getParcelableArrayListExtra(AppConstants.IntentKey.CATEGORY_DATA)!!)
            setSelectTradeListAdapter()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.routChatTab -> setupTab(0)
        }
    }

    private fun setSelectTradeListAdapter() {
        if (listCategories.size > 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.setHasFixedSize(true)
            adapter = SelectTradeListAdapter(mContext, listCategories, tradeId)
            binding.recyclerView.adapter = adapter
        } else {
            binding.recyclerView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        searchViewItem = menu.findItem(R.id.action_search)
        searchView = searchViewItem.actionView as SearchView
        searchView.queryHint = getString(R.string.lbl_search)
        searchView.isFocusable = true
        searchView.isIconified = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter?.filter(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun selectCategory(tradeId: Int?, tradeName: String?) {
        val intent = Intent()
        intent.putExtra(AppConstants.IntentKey.CATEGORY_ID, tradeId)
        intent.putExtra(AppConstants.IntentKey.CATEGORY_TITLE, tradeName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}