package com.app.otmjobs.dashboard.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.dashboard.ui.adapter.TradesmanListAdapter
import com.app.otmjobs.databinding.FragmentUsersBinding

class UserFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var mContext: Context

    companion object {
        fun newInstance(): UserFragment {
            return UserFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        mContext = requireActivity()
        setTradesmanListAdapter()
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun setTradesmanListAdapter() {
        binding.rvUsersList.setHasFixedSize(true)
        var adapter: TradesmanListAdapter = TradesmanListAdapter(mContext, null)
        binding.rvUsersList.adapter = adapter
    }
}