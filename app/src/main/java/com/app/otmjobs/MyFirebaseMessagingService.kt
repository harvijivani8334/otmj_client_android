package com.app.otmjobs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.otmjobs.common.data.model.FcmData
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var data: FcmData? = null
    var mContext: Context? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        mContext = this
        Log.e("test","onMessageReceived");
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            //        Log.e("test", "TITLE:" + remoteMessage.getNotification().getTitle());
//        Log.e("test", "BODY:" + remoteMessage.getNotification().getBody());
            try {
                data = FcmData()
                data!!.body = remoteMessage.data["body"]
                data!!.type = remoteMessage.data["notification_type"]
                data!!.timestamp=remoteMessage.data["timestamp"]
                data!!.title = remoteMessage.data["title"]
                sendNotification(data!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }


    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }

    private fun sendNotification(data: FcmData) {
        val CHANNEL_ID = resources.getString(R.string.default_notification_channel_id)
        val pendingIntent = redirectToActivity(data)
        if (pendingIntent != null) {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_white_120)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(Html.fromHtml(data.title)) //                    .setContentTitle(getString(R.string.app_name))
                .setContentText(Html.fromHtml(data.body))
                .setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(data.body)))
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    AppConstants.EXTRA_CHANNEL_SID,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
            val m = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            notificationManager.notify(m /* ID of notification */, notificationBuilder.build())
        }
    }

    private fun redirectToActivity(data: FcmData): PendingIntent? {
        var intent: Intent? = null
        intent = if (AppUtils.getUserPreference(this@MyFirebaseMessagingService) == null) {
            Intent("com.app.otmjobs.authentication.ui.activity.LoginActivity")
        } else {
            AppUtils.getFcmIntent(data)
        }
        if (intent != null) {
            intent.putExtra(AppConstants.IntentKey.IS_FROM_NOTIFICATION, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        return if (intent != null) {
            PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
            )
        } else null
    }
}