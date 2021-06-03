package com.app.otmjobs.common.ui.fragment

import androidx.fragment.app.Fragment
import com.app.otmjobs.common.callback.OnFragmentBackListener

class BackPressImpl(private val parentFragment: Fragment?) :
    OnFragmentBackListener {
    override fun onBackPressed(): Boolean {
        if (parentFragment == null) return false
        val childCount = parentFragment.childFragmentManager.backStackEntryCount
        return if (childCount == 0) {
            // it has no child Fragment
            // can not handle the onBackPressed task by itself
            false
        } else {
            // get the child Fragment
            val childFragmentManager = parentFragment.childFragmentManager
            val childFragment = childFragmentManager.fragments[0] as OnFragmentBackListener

            // propagate onBackPressed method call to the child Fragment
            if (!childFragment.onBackPressed()) {
                // child Fragment was unable to handle the task
                // It could happen when the child Fragment is last last leaf of a chain
                // removing the child Fragment from stack
                childFragmentManager.popBackStackImmediate()
            }

            // either this Fragment or its child handled the task
            // either way we are successful and done here
            true
        }
    }
}