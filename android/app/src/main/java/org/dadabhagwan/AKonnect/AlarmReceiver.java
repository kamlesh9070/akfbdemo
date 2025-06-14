package org.dadabhagwan.AKonnect;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.InitAppResponse;

public class AlarmReceiver extends BroadcastReceiver implements AsyncResponseListner {

  protected static final String TAG = "AKonnect[AlarmReceiver]";
  Context context = null;

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "Inside AlarmReceiver.onReceive ");
    boolean AlarmActiveFlag = true;
    try {
      InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
      Log.d(TAG, "################# initAppResponse:" + initAppResponse);
      AlarmActiveFlag = initAppResponse.isAlarmActiveFlag();
      if (ApplicationUtility.isOnline(context) && AlarmActiveFlag) {
        fetchMsgFromServer(context);
      }
    } catch (Exception e) {
      Log.e(TAG, "Error inside AlarmReceiver.onReceive  message." + e.getMessage(), e);
      e.printStackTrace();
    }
  }

  private void fetchMsgFromServer(Context context) {
    Log.d(TAG, "Inside AlarmReceiver.fetchMsgFromServer ");
    try {
      long currentTimestamp = ApplicationUtility.getCurrentTimestamp();
      InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      long lastSeenTimestamp = sharedPreferencesTask.getLong(SharedPrefConstants.LAST_SEEN_TIMESTAMP);
      int repeatAlarmTimeInMin = initAppResponse.getRepeatAlarmTimeInMinutes();
      //Temp
      //if(true) {
      if ((currentTimestamp - lastSeenTimestamp) > repeatAlarmTimeInMin * 60 * 1000) {
        WebServiceCall.fetchMsgFromServer(context, this);
      } else {
        Log.d(TAG, "Inside Else condition fetchMsgFromServer, where Device Last seen on " + new java.util.Date(lastSeenTimestamp));
      }
      this.context = context;
    } catch (Exception e) {
      Log.e(TAG, "Error inside AlarmReceiver message." + e.getMessage(), e);
      e.printStackTrace();
    }
  }

  @Override
  public void onPostExecute(String request, String out) {
    try {
      ApplicationUtility.handlePullNotificationRes(context, request, out);
      AlarmSetupReceiver.setAlarm(context);
    } catch (Exception e) {
      Log.e(TAG, "processFinish output Exception:: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
