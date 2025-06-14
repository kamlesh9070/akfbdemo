// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.dadabhagwan.AKonnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.dadabhagwan.AKonnect.constants.WSConstant;
import org.dadabhagwan.AKonnect.dto.NotificationDTO;

import java.util.HashMap;
import java.util.Map;

public class AKFlutterFirebaseMessagingReceiver extends BroadcastReceiver {
  private static final String TAG = "AKFLTFireMsgReceiver";
  static HashMap<String, RemoteMessage> notifications = new HashMap<>();
  private Context backgroundContext;
  private Context context;
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "############### broadcast received for message");
    Log.d(TAG, "$$$$$$$$$$$$$ Changed For Log lastmsgid");
    /*if (ContextHolder.getApplicationContext() == null) {
      ContextHolder.setApplicationContext(context.getApplicationContext());
    }*/
    backgroundContext = context.getApplicationContext();
    this.context = context;
    if (intent.getExtras() == null) {
      Log.d(
          TAG,
          "broadcast received but intent contained no extras to process RemoteMessage. Operation cancelled.");
      return;
    }

    RemoteMessage remoteMessage = new RemoteMessage(intent.getExtras());

    // Store the RemoteMessage if the message contains a notification payload.
    if (remoteMessage.getNotification() != null) {

      notifications.put(remoteMessage.getMessageId(), remoteMessage);
//      FlutterFirebaseMessagingStore.getInstance().storeFirebaseMessage(remoteMessage);
    }

    sendAkonnectNotification(remoteMessage);

    //  |-> ---------------------
    //      App in Foreground
    //   ------------------------
    /*if (FlutterFirebaseMessagingUtils.isApplicationForeground(context)) {
      FlutterFirebaseRemoteMessageLiveData.getInstance().postRemoteMessage(remoteMessage);
      return;
    }*/

    //  |-> ---------------------
    //    App in Background/Quit
    //   ------------------------
    /*Intent onBackgroundMessageIntent =
        new Intent(context, FlutterFirebaseMessagingBackgroundService.class);
    onBackgroundMessageIntent.putExtra(
        FlutterFirebaseMessagingUtils.EXTRA_REMOTE_MESSAGE, remoteMessage);
    FlutterFirebaseMessagingBackgroundService.enqueueMessageProcessing(
        context, onBackgroundMessageIntent);*/
  }
  void sendAkonnectNotification(final RemoteMessage remoteMessage) {
    Log.v(TAG, "==> MyFirebaseMessagingService onMessageReceived remoteMessage ::  " + remoteMessage.toString());

    if (remoteMessage.getNotification() != null) {
      Log.v(TAG, "\tNotification Title: " + remoteMessage.getNotification().getTitle());
      Log.v(TAG, "\tNotification Message: " + remoteMessage.getNotification().getBody());
    }
    Log.v(TAG, "#### remoteMessage Data:" + remoteMessage.getData().toString());
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("wasTapped", false);
    for (String key : remoteMessage.getData().keySet()) {
      Object value = remoteMessage.getData().get(key);
      Log.v(TAG, "\tKey: " + key + " Value: " + value);
      data.put(key, value);
    }
    Log.v(TAG, "\tNotification Data: " + data.toString());
    sendNotification(data);
  }

  /**
   * Create and show a simple notification containing the received FCM message.
   *
   * @param data FCM message body received.
   */
  //private void sendNotification(String title, String messageBody, Map<String, String> data) {
  private void sendNotification(Map<String, Object> data) {
    try {
      Log.d(TAG, "data:" + data);
      Gson gson = new Gson();
      JsonElement jsonElement = gson.toJsonTree(data);
      NotificationDTO nDTO = gson.fromJson(jsonElement, NotificationDTO.class);
      Log.d(TAG, "nDTO:" + nDTO);
      int notId = 0;
      if (!ApplicationUtility.isStrNullOrEmpty(nDTO.getNotificationTitle(backgroundContext))) {
        Log.v(TAG, "FCM AKonnectNotificationManager.sendStackNotification " + nDTO.toString());
        new AKonnectNotificationManager(context, nDTO).sendStackNotification(WSConstant.PUSHTYPE_FCM);
      }
    } catch (Exception e) {
      Log.e(TAG, "Exception in Firebase AKonnectNotificationManager.sendStackNotification " + e.getStackTrace().toString());
    }
  }
}
