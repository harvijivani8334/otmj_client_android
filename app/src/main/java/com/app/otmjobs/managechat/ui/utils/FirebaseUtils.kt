package com.app.otmjobs.managechat.ui.utils

import android.content.Context
import android.util.Log
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.managechat.data.model.MessageInfo
import com.app.otmjobs.managechat.data.model.UserInfo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseUtils {
    suspend fun getUserDetails(userId: String): UserInfo {
        var userInfo: UserInfo? = null
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userRef = db.collection("users")
            .whereEqualTo("_id", userId)
        val userdata = userRef.get().await()
        for (document in userdata.documents) {
            userInfo = document.toObject(UserInfo::class.java)
        }
        Log.d("test", "end")
        return userInfo!!;
    }

    suspend fun getChannelALlMessages(roomId: String): MutableList<MessageInfo> {
        val list: MutableList<MessageInfo> = ArrayList()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val query = db.collection(AppConstants.FCM_ROOM).document(roomId)
            .collection(AppConstants.FCM_MESSAGES)
            .orderBy("timestamp")
        val messages = query.get().await()
        for (document in messages.documents) {
            val messageInfo = document.toObject(MessageInfo::class.java)
            list.add(messageInfo!!)
        }
        Log.d("test", "end")
        return list
    }

    fun setOnlineStatus(mContext: Context, isOnline: Boolean) {
        if (AppUtils.getUserPreference(mContext) != null) {
            val currentUser = AppUtils.getFirebaseUserId(mContext)
            val database = FirebaseDatabase.getInstance().reference
                .child("status").child(currentUser)
            val childUpdates = HashMap<String, Any>()

            if (isOnline) {
                childUpdates["state"] = "online"
                childUpdates["lastChanged"] = 0
            } else {
                childUpdates["state"] = "offline"
                childUpdates["lastChanged"] = ServerValue.TIMESTAMP
            }
            database.updateChildren(childUpdates)
        }
    }
}