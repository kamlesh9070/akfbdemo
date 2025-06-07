package com.example.akfbdemo

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "From: ${remoteMessage.from}")

        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data: ${remoteMessage.data}")
        }

        // Handle notification payload
        remoteMessage.notification?.let {
            Log.d("FCM", "Message body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        // Send token to your server here
    }
}