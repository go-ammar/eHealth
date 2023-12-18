package com.project.projecte_health.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.projecte_health.R
import timber.log.Timber

class FirebaseMessagingService : FirebaseMessagingService() {


    val MESSAGE_CHANNEL_ID = "message_channel_id"
    val MESSAGE_CHANNEL_NAME = "message"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TOKEN-- ", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        try {
            Log.d("MyData", "---" + remoteMessage.data)
            Timber.d("Message Received From Firebase: ${remoteMessage.data}")
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body
            var errorCode = ""
            val data = remoteMessage.data
            if (data != null) {
                if (remoteMessage.notification != null) {
                    val title = remoteMessage.notification!!.title
                    val body = remoteMessage.notification!!.body

                    // Use title and body to display the notification
                    showNotification(title, body)
                }
                Log.d("TAG", "onMessageReceived: " + data)
            }
        } catch (e: Exception) {
            Log.e("FirebaseFCMService", e.localizedMessage)
        }

    }
    @SuppressLint("MissingPermission")
    private fun showNotification(title: String?, body: String?) {
        val channelId = "your_channel_id" // Choose a unique channel ID
        val notificationId = 1 // Choose a unique notification ID

        // Create a notification channel for devices running Android Oreo (API 26) and above
        createNotificationChannel(channelId)

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_home_fill) // Replace with your app's notification icon
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Your Channel Name", // Replace with your channel name
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}