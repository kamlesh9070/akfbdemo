package org.dadabhagwan.AKonnect;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.InitAppResponse;

public class InternetServiceConnectivityReceiver extends BroadcastReceiver implements AsyncResponseListner {

  public static final String PREFS_NAME = "PushyRestartPrefs";
  private static final String Last_Pushy_Restart = "LastPushyRestart";
  private static final long Min_Restart_Interval = 5 * 60 * 1000; // 5 Mins
  private static final String TAG = "AKonnect[Conn]";
  Context context = null;

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "Inside InternetServiceConnectivityReceiver.onReceive ");
    boolean AlarmActiveFlag = true;
    try {
      InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
      Log.d(TAG, "initAppResponse:" + initAppResponse);
      if(initAppResponse != null) {
        AlarmActiveFlag = SharedPreferencesTask.getInitAppResponse(context).isAlarmActiveFlag();
        if (ApplicationUtility.isOnline(context) == true && AlarmActiveFlag) {
          fetchMsgFromServer(context);
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "Error inside InternetServiceConnectivityReceiver message." + e.getMessage(), e);
      e.printStackTrace();
    }
  }


  private void fetchMsgFromServer(Context context) {
    Log.d(TAG, "Inside InternetServiceConnectivityReceiver.fetchMsgFromServer ");
    try {
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      long lastSeenTimestamp = sharedPreferencesTask.getLong(SharedPrefConstants.LAST_SEEN_TIMESTAMP);
      InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
      int repeatAlramTimeInMin = initAppResponse.getRepeatAlarmTimeInMinutes();
      long currentTimestamp = ApplicationUtility.getCurrentTimestamp();

      // We only call API if time difference is greater than Alarm Interval time
      if ((currentTimestamp - lastSeenTimestamp) > (repeatAlramTimeInMin * 60 * 1000)) {
        WebServiceCall.fetchMsgFromServer(context, this);
      } else {
        Log.d(TAG, "Inside Else condition fetchMsgFromServer, where Device Last seen on " + new java.util.Date(lastSeenTimestamp));
      }
      this.context = context;
    } catch (Exception e) {
      Log.e(TAG, "Error inside InternetServiceConnectivityReceiver message." + e.getMessage(), e);
      e.printStackTrace();
    }

  }

  //Here you will receive the result fired from async class of onPostExecute(result) method.
  @Override
  public void onPostExecute(String request, String output) {
    try {
      ApplicationUtility.handlePullNotificationRes(context, request, output);
      AlarmSetupReceiver.setAlarm(context);
    } catch (Exception e) {
      Log.e(TAG, "processFinish output Exception:: " + e.getMessage());
      e.printStackTrace();
    }

  }

}
