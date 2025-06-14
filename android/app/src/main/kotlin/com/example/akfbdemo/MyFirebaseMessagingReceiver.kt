package com.example.akfbdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import java.util.HashMap

class MyFirebaseMessagingReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "MyFirebaseMessagingReceiver"
        // Consider using a thread-safe collection if accessed from multiple threads concurrently.
        // For simplicity, sticking to HashMap as in the original Java code.
        val notifications = HashMap<String?, RemoteMessage>()
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "broadcast 111 received for message")

    }
}