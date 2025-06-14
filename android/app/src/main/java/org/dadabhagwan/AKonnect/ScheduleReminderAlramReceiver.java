package org.dadabhagwan.AKonnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by coder2 on 19-May-2018.
 */

public class ScheduleReminderAlramReceiver extends BroadcastReceiver {

  protected static final String TAG = "AKonnect[PushyAlarm]";

  @Override
  public void onReceive(Context context, Intent intent) {
    scheduleReminder(context);
  }

  public void scheduleReminder(Context context) {
    try {

      System.out.println(TAG + "\t&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Inside ScheduleReminderAlramReceiver scheduleReminder ");
      System.out.println(TAG + "\tStart ScheduleReminderAlramReceiver.scheduleReminder");
      Log.d(TAG, "Start ScheduleReminderAlramReceiver.scheduleReminder");

    } catch (Exception e) {
      System.out.println(TAG + "\tError inside ScheduleReminderAlramReceiver message." + e.getMessage());
      Log.e(TAG, "Error inside ScheduleReminderAlramReceiver message." + e.getMessage(), e);
      try {
        Toast.makeText(context, "Someting Wrong happend while pushy alarm receiver", Toast.LENGTH_LONG).show();
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
    System.out.println(TAG + "\tEnd ScheduleReminderAlramReceiver.scheduleReminder");
    Log.d(TAG, "End ScheduleReminderAlramReceiver.scheduleReminder");
  }

}
