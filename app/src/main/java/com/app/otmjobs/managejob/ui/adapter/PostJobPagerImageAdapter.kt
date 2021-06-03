package com.app.otmjobs.managejob.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.R
import com.app.otmjobs.databinding.RowPostJobPagerImageBinding
import com.app.otmjobs.managejob.data.model.JobImageInfo
import com.app.utilities.utils.StringHelper

class PostJobPagerImageAdapter(var mContext: Context, var list: MutableList<JobImageInfo>) :
    PagerAdapter() {
    private lateinit var layoutInflater: LayoutInflater

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding: RowPostJobPagerImageBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.row_post_job_pager_image,
            container,
            false
        )
        setImage(list.get(position).file_name, binding.imgSlide)
        container.addView(binding.root)
        return binding.root
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View?
        container.removeView(view)
    }

    private fun setImage(imageUrl: String?, imageView: AppCompatImageView) {
        if (!StringHelper.isEmpty(imageUrl)) {
            GlideUtil.loadImage(
                imageUrl,
                imageView,
                null,
                null,
                Constant.ImageScaleType.FIT_CENTER,
                null
            )
        }
    }
}