package com.app.otmjobs.managejob.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.ActivityJobHistoryBinding
import com.app.otmjobs.managejob.ui.adapter.JobHistoryListAdapter

class JobHistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityJobHistoryBinding
    private lateinit var mContext: Context;
    private var adapter: JobHistoryListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_history)
        setStatusBarColor()
        mContext = this
        setupToolbar(getString(R.string.job_history), true)
        setAdapter()
    }

//    private fun getIntentData() {
//        if (intent != null && intent.extras != null) {
//            tradeId = intent.getIntExtra(AppConstants.IntentKey.CATEGORY_ID, 0);
//            listCategories.addAll(intent.getParcelableArrayListExtra(AppConstants.IntentKey.CATEGORY_DATA)!!)
//            setSelectTradeListAdapter()
//        }
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.routChatTab -> setupTab(0)
        }
    }

    private fun setAdapter() {
//        if (listCategories.size > 0) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.recyclerView.setHasFixedSize(true)
        adapter = JobHistoryListAdapter(mContext, null, null)
        binding.recyclerView.adapter = adapter
//        } else {
//            binding.recyclerView.visibility = View.GONE
//        }
    }

    fun selectCategory(tradeId: Int?, tradeName: String?) {
        val intent = Intent()
        intent.putExtra(AppConstants.IntentKey.CATEGORY_ID, tradeId)
        intent.putExtra(AppConstants.IntentKey.CATEGORY_TITLE, tradeName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}