package com.app.otmjobs.common.ui.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager
    , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    private val registeredFragments = SparseArray<Fragment>()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    //    @Override
    //    public int getItemPosition(Object object) {
    //        /*
    //         * called when the fragments are reordered to get the
    //         * changes.
    //         */
    //        int idx = mFragmentList.indexOf(object);
    //        return idx < 0 ? POSITION_NONE : idx;
    //    }
    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFrag(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
        notifyDataSetChanged()
    }

    fun addFragAt(fragment: Fragment, title: String, position: Int) {
        mFragmentList.add(position, fragment)
        mFragmentTitleList.add(position, title)
        notifyDataSetChanged()
    }

    fun removeFrag(position: Int) {
        mFragmentList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments[position]
    }

    fun getmFragmentList(): List<Fragment> {
        return mFragmentList
    }
}