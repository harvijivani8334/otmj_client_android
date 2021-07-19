package com.app.otmjobs.dashboard.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.authentication.data.model.User
import com.app.otmjobs.authentication.ui.activity.EditProfileActivity
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.adapter.ViewPagerAdapter
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.fragment.ChatFragment
import com.app.otmjobs.dashboard.ui.fragment.MoreFragment
import com.app.otmjobs.databinding.ActivityDashboardBinding
import com.app.otmjobs.databinding.ContentDashboardBinding
import com.app.otmjobs.managechat.ui.fragment.UserChatFragment
import com.app.otmjobs.managejob.ui.fragment.HomeFragment
import com.app.otmjobs.managejob.ui.fragment.MyJobsFragment
import com.app.otmjobs.managejob.ui.fragment.UserFragment
import com.app.utilities.utils.StringHelper
import com.app.utilities.utils.ViewPagerDisableSwipe
import kotlinx.android.synthetic.main.app_bar_dashboard.view.*

class DashBoardActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var mContext: Context;
    private lateinit var pagerAdapter: ViewPagerAdapter
    private var selectedTabIndex: Int = 2
    private var bindingContent: ContentDashboardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        bindingContent = DataBindingUtil.bind(binding.appBarLayout.contentDashboard.root)

        setStatusBarColor()
        mContext = this
        setupViewPager(bindingContent!!.viewPager);

        bindingContent!!.routChatTab.setOnClickListener(this)
        bindingContent!!.routStorage.setOnClickListener(this)
        bindingContent!!.routHomeTab.setOnClickListener(this)
        bindingContent!!.routUsers.setOnClickListener(this)
        bindingContent!!.routMoreTab.setOnClickListener(this)
        binding.drawerLayout.imgUser.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.routChatTab -> setupTab(0)
            R.id.routStorage -> setupTab(1)
            R.id.routHomeTab -> setupTab(2)
            R.id.routUsers -> setupTab(3)
            R.id.routMoreTab -> setupTab(4)
            R.id.imgUser -> moveActivity(
                mContext,
                EditProfileActivity::class.java,
                false,
                false,
                null
            )
        }
    }

    private fun setupViewPager(viewPager: ViewPagerDisableSwipe) {
        viewPager.setPagingEnabled(false)
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.addFrag(UserChatFragment.newInstance(), "")
        pagerAdapter.addFrag(MyJobsFragment.newInstance(), "")
        pagerAdapter.addFrag(HomeFragment.newInstance(), "")
        val bundle = Bundle()
        bundle.putInt(
            AppConstants.IntentKey.JOB_ID,
            0
        )
        pagerAdapter.addFrag(UserFragment.newInstance(bundle), "")
        pagerAdapter.addFrag(MoreFragment.newInstance(), "")
        viewPager.adapter = pagerAdapter
        setupTab(selectedTabIndex)
        viewPager.offscreenPageLimit = 5

    }

    private fun setupTab(position: Int) {
        selectedTabIndex = position
        resetTabColor()
        bindingContent!!.viewPager.currentItem = position

        when (position) {
            0 -> {
                bindingContent!!.imgChatTab.setImageResource(R.drawable.ic_active_chat_tab)
                setupHomeButton(false, getString(R.string.chat), false)
            }
            1 -> {
                bindingContent!!.imgFolder.setImageResource(R.drawable.ic_active_storage_tab)
                setupHomeButton(false, getString(R.string.my_jobs), false)
            }
            2 -> {
                bindingContent!!.imgHomeTab.setImageResource(R.drawable.ic_active_home_tab)
                setupHomeButton(false, getString(R.string.post_job), false)
            }
            3 -> {
                bindingContent!!.imgUserTab.setImageResource(R.drawable.ic_active_users_tab)
                setupHomeButton(false, getString(R.string.tradesman), false)
            }
            4 -> {
                bindingContent!!.imgMoreTab.setImageResource(R.drawable.ic_active_more_tab)
                setupHomeButton(false, getString(R.string.more), false)
            }
        }

    }

    private fun resetTabColor() {
        bindingContent!!.imgChatTab.setImageResource(R.drawable.ic_inactive_chat_tab)
        bindingContent!!.imgFolder.setImageResource(R.drawable.ic_inactive_storage_tab)
        bindingContent!!.imgHomeTab.setImageResource(R.drawable.ic_inactive_home_tab)
        bindingContent!!.imgUserTab.setImageResource(R.drawable.ic_inactive_users_tab)
        bindingContent!!.imgMoreTab.setImageResource(R.drawable.ic_inactive_more_tab)
    }

    private fun setupHomeButton(isBackEnable: Boolean, title: String?, isClearTitle: Boolean) {
        collapseActionView()
        if (isBackEnable) {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        }
        val txtTitle: TextView = findViewById(R.id.toolBarNavigation)
        if (!StringHelper.isEmpty(title))
            txtTitle.text =
                title
        else if (isClearTitle) txtTitle.text =
            ""
    }

    private fun collapseActionView() {
        try {
            binding.appBarLayout.toolbar.collapseActionView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUserImage() {
        val user: User? = AppUtils.getUserPreference(mContext)
        if (user != null) {
            AppUtils.setUserImage(mContext, user.image, binding.drawerLayout.toolbar.imgUser)
        }
    }

    fun refreshMyJobsFragment() {
        for (i in pagerAdapter.getmFragmentList().indices) {
            if (pagerAdapter.getmFragmentList()[i] is MyJobsFragment) {
                (pagerAdapter.getmFragmentList()[i] as MyJobsFragment).loadData(false)
            }
        }
    }

    fun updateChatCount(count: Int) {
        if (count > 0) {
            bindingContent!!.txtMessageCount.visibility = View.VISIBLE
            bindingContent!!.txtMessageCount.text = count.toString()
        } else {
            bindingContent!!.txtMessageCount.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        setUserImage()
    }
}