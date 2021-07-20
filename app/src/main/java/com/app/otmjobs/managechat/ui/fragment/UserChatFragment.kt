package com.app.otmjobs.managechat.ui.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.otmjobs.R
import com.app.otmjobs.common.ui.fragment.BaseFragment
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.dashboard.ui.activity.DashBoardActivity
import com.app.otmjobs.databinding.FragmentChatBinding
import com.app.otmjobs.databinding.RowUsersChatListBinding
import com.app.otmjobs.managechat.data.model.ChannelInfo
import com.app.otmjobs.managechat.data.model.MessageInfo
import com.app.otmjobs.managechat.data.model.UserInfo
import com.app.otmjobs.managechat.ui.activity.ChatActivity
import com.app.otmjobs.managechat.ui.utils.FirebaseUtils
import com.app.utilities.utils.StringHelper
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.parceler.Parcels
import java.util.*
import kotlin.collections.ArrayList


class UserChatFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentChatBinding
    private lateinit var mContext: Context
    private lateinit var db: FirebaseFirestore
    private var currentUser: String = ""
    private var adapter: ChannelListAdapter? = null
    private val users: HashMap<String, MutableList<UserInfo>> = HashMap()

    companion object {
        fun newInstance(): UserChatFragment {
            return UserChatFragment()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        mContext = requireActivity()
        db = FirebaseFirestore.getInstance()
        currentUser = AppUtils.getFirebaseUserId(mContext)
        getAllChannel()

        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

//    private fun setUsersChatListAdapter() {
//        binding.rvUsersChatList.setHasFixedSize(true)
//        val adapter = UsersChatListAdapter(mContext, null)
//        binding.rvUsersChatList.adapter = adapter
//    }

    private fun getAllChannel() {
        val query = db.collection(AppConstants.FCM_ROOM)
            .whereArrayContainsAny(AppConstants.FCM_USERS, listOf(currentUser))
        val options =
            FirestoreRecyclerOptions.Builder<ChannelInfo>().setQuery(query, ChannelInfo::class.java)
                .build()
        adapter = ChannelListAdapter(options)
        binding.rvUsersChatList.adapter = adapter

    }

    class MyViewHolder(binding: RowUsersChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding: RowUsersChatListBinding? = binding
    }

    inner class ChannelListAdapter internal constructor(options: FirestoreRecyclerOptions<ChannelInfo>) :
        FirestoreRecyclerAdapter<ChannelInfo, MyViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater = LayoutInflater.from(parent.context)

            val binding: RowUsersChatListBinding =
                DataBindingUtil.inflate(inflater, R.layout.row_users_chat_list, parent, false)
            return MyViewHolder(binding)
        }


        override fun onBindViewHolder(holder: MyViewHolder, position: Int, info: ChannelInfo) {
            val binding: RowUsersChatListBinding = holder.binding!!

            binding.routMainView.setOnClickListener {
                val bundle = Bundle()
                info.roomId = snapshots.getSnapshot(position).id
                bundle.putParcelable(AppConstants.IntentKey.CHANNEL_INFO, Parcels.wrap(info))
                moveActivity(mContext, ChatActivity::class.java, false, false, bundle)
            }
//            if (info.typingUsers.isNotEmpty()) {
//                binding.txtTyping.visibility = View.VISIBLE
//            } else {
//                binding.txtTyping.visibility = View.GONE
//            }
            if (!StringHelper.isEmpty(info.roomName)) {
                binding.txtJobTitle.visibility = View.VISIBLE
                binding.txtJobTitle.text = info.roomName
            } else {
                binding.txtJobTitle.visibility = View.GONE
            }

            setUserProfile(info.users, binding)

            GlobalScope.launch(Dispatchers.IO) {
                getUnreadCount(
                    snapshots.getSnapshot(position).id,
                    binding.txtMessageCount,
                    position
                )
            }

//            setLastMessage(snapshots.getSnapshot(position).id, binding.txtLastMessage)
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun onDataChanged() {
            Log.e("test", "onDataChanged");
//            binding.rvUsersChatList.layoutManager!!.scrollToPosition(itemCount - 1)
//            unreadMessageCount = 0
//            adapter!!.notifyDataSetChanged()

            GlobalScope.launch(Dispatchers.IO) {
                val totalCount = getTotalUnreadCount(adapter!!.snapshots)
                requireActivity().runOnUiThread {
                    (requireActivity() as DashBoardActivity?)?.updateChatCount(
                        totalCount
                    )
                }
            }
        }

        private fun setUserProfile(users: List<String>, binding: RowUsersChatListBinding) {
            if (users.isNotEmpty()) {
                var userId = "";
                for (id in users) {
                    if (id != currentUser)
                        userId = id
                }
                GlobalScope.launch(Dispatchers.IO) {
                    val userInfo: UserInfo = FirebaseUtils.getUserDetails(userId)!!
                    activity?.runOnUiThread {
                        binding.txtUserName.text = userInfo.username
                        val imageUrl = AppConstants.SERVER_IMAGE_PATH + userInfo.avatar
                        AppUtils.setUserImage(mContext, imageUrl, binding.imgUser)
                    }

                }
            }
        }

        private fun setLastMessage(roomId: String, textVew: TextView) {
            val query = db.collection(AppConstants.FCM_ROOM).document(roomId)
                .collection(AppConstants.FCM_MESSAGES)
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
            query.addSnapshotListener { value, error ->
                var info: MessageInfo? = null
                for (document in value!!.documents) {
                    info = document.toObject(MessageInfo::class.java)!!
                }
                if (info != null) {
                    if (!isSeenMessage(info.seen, info.sender_id)) {
                        textVew.setTextColor(activity!!.resources.getColor(R.color.colorPrimaryText))
                        textVew.typeface = Typeface.create(
                            mContext.getString(R.string.font_family_medium),
                            Typeface.NORMAL
                        )
                    } else {
                        textVew.setTextColor(activity!!.resources.getColor(R.color.colorSecondaryExtraLightText))
                        textVew.typeface = Typeface.DEFAULT
                    }

                    if (info.file != null && !StringHelper.isEmpty(info.file?.url))
                        textVew.text = "Received Image"
                    else
                        textVew.text = info.content
                } else {
                    textVew.typeface = Typeface.DEFAULT
                    textVew.setTextColor(activity!!.resources.getColor(R.color.colorSecondaryExtraLightText))
                    textVew.text = "Chat Initialized"
                }
            }
        }


        private suspend fun getUnreadCount(roomId: String, textVew: TextView, position: Int): Int {
            val messages: MutableList<MessageInfo> = FirebaseUtils.getChannelALlMessages(roomId)
            var count = 0
            for (message in messages) {
                if (!isSeenMessage(message.seen, message.sender_id))
                    count += 1
            }

            requireActivity().runOnUiThread {
                if (count > 0) {
                    textVew.visibility = View.VISIBLE
                    textVew.text = count.toString()
                } else {
                    textVew.visibility = View.GONE
                }
            }

            return count
        }
    }

    private suspend fun getTotalUnreadCount(channels: MutableList<ChannelInfo>?): Int {
        var totalCount = 0
        if (channels != null) {
            try {
                for (i in adapter!!.snapshots.indices) {
                    val roomId = adapter!!.snapshots.getSnapshot(i).id
                    val messages: MutableList<MessageInfo> =
                        FirebaseUtils.getChannelALlMessages(roomId)
                    var count = 0
                    for (message in messages) {
                        if (!isSeenMessage(message.seen, message.sender_id))
                            count += 1
                    }
                    totalCount += count
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return totalCount
    }

    private fun isSeenMessage(seen: HashMap<String, Date?>?, sender: String): Boolean {
        var isSeen = false
        if (sender == currentUser) {
            isSeen = true
        } else {
            if (seen != null) {
                for (i in seen.keys) {
                    if (i == currentUser) {
                        isSeen = true
                        break
                    }
                }
            }
        }
        return isSeen
    }


   /* suspend fun getChannelAllUsersDetails() {
        if (adapter != null) {
//            users.clear()
            for (i in adapter!!.snapshots.indices) {
                val info = adapter!!.getItem(i)
                val listUserInfo: MutableList<UserInfo> = ArrayList()
                for (user in info.users) {
                    val userInfo: UserInfo = FirebaseUtils.getUserDetails(user)
                    listUserInfo.add(userInfo)
                }
                users[adapter!!.snapshots.getSnapshot(i).id] = listUserInfo
                Log.e("test", "" + users[adapter!!.snapshots.getSnapshot(i).id]!!.size);
                Log.e("test", "-----------------------------");
            }
            Log.e("test", "users size:" + users.size);
        }
    }*/

    override fun onStart() {
        super.onStart()

        if (adapter != null) {
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

}