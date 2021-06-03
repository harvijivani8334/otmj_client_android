package com.app.otmjobs.managejob.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.ActivityPostJobPagerImageBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.otmjobs.managejob.ui.adapter.PostJobPagerImageAdapter

class PostJobPagerImagesActivity : BaseActivity() {
    private lateinit var binding: ActivityPostJobPagerImageBinding
    private lateinit var mContext: Context;
    val list: MutableList<JobImageInfo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_job_pager_image)
        setStatusBarColor()
        mContext = this
        setupToolbar("", true)

        binding.vpPhotos.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                    binding.txtImageCount.text = (position+1).toString()+"/"+list.size
            }

        })
        getIntentData()
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            list.addAll(intent.getParcelableArrayListExtra(AppConstants.IntentKey.POST_JOB_PHOTOS)!!)
            setPagerAdapter()
        }
    }

    private fun setPagerAdapter() {
        val adapterPager = PostJobPagerImageAdapter(mContext, list)
        binding.vpPhotos.adapter = adapterPager
        binding.txtImageCount.text = "1/"+list.size
    }

}