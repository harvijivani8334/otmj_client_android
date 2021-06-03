package com.app.otmjobs.dashboard.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.dashboard.ui.adapter.UsersChatListAdapter
import com.app.otmjobs.databinding.FragmentChatBinding

class ChatFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentChatBinding
    private lateinit var mContext: Context

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
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
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        mContext = requireActivity()
        setUsersChatListAdapter()
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    private fun setUsersChatListAdapter() {
        binding.rvUsersChatList.setHasFixedSize(true)
        var adapter: UsersChatListAdapter = UsersChatListAdapter(mContext, null)
        binding.rvUsersChatList.adapter = adapter
    }

}