package org.dadabhagwan.AKonnect;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import io.flutter.plugins.firebase.messaging.R;

import android.util.Log;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.NotificationDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import me.pushy.sdk.PushReceiver;

import org.dadabhagwan.AKonnect.dbo.DBHelper;


/**
 * Created by coder2 on 14-Feb-2018.
 */

public class AKonnectNotificationManager {

  private static final String TAG = "AKonnect[NotManager]";
  private static int i = 0;
  //private static final int color = 0xb71d13;
  private static final int color = 0xFF4F9FFF;
  private static final String group = "AKONNECTGROUP";
  private static final String NOTIFICATION_CHANNEL_GROUP_AKONNECT_ID = "AKONNECT";
  private static final String NOTIFICATION_CHANNEL_GROUP_AKONNECT_DESC = "AKonnect Senders";
  private static final String NOTIFICATION_CHANNEL_GROUP_SYSTEM_ID = "SYSTEM";
  private static final String NOTIFICATION_CHANNEL_GROUP_SYSTEM_DESC = "System";
  private static final String NOTIFICATION_CHANNEL_DESC_SILENT = "Silent Notification";
  private static final String NOTIFICATION_CHANNEL_ID_SILENT = "SILENT";
  private static final int AKONNECT_GROUP_ID = 2000;
  private final static int AKONNECT_NOT_ID = 2020;
  private static final long lDurationBtwTwoNotiForSound = 15 * 1000;
  private static final String RECALL_MSG = "This message was deleted.";
  private static final String MESSAGE_ID = "MESSAGE_ID";
  //vibrate
  long[] vibrate = {500, 1000};

  static Date previousNotificationTime = null;
  static long waitMillSecond = 0;

  private Context context;
  private NotificationDTO nDTO;
  private static Map<String, String> nameByChannelId = new ConcurrentHashMap<String, String>();
  private DBHelper dbHelper;

  public AKonnectNotificationManager(Context context, NotificationDTO notificationDTO) {
    this.context = context;
    this.nDTO = notificationDTO;

  }

  public void sendStackNotification(String pushType) {
    boolean isMsgIdExist = false;
    boolean AlarmActiveFlag = false;
    try {
      //boolean isDeviceManufacturerSupported = ApplicationUtility.isDeviceManufacturerSupported(context);
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      SharedPreferencesTask nameByChannelIdPref = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_SENDER_ALIAS_MASTER_PREF);
      if (SharedPreferencesTask.getInitAppResponse(context) != null)
        AlarmActiveFlag = SharedPreferencesTask.getInitAppResponse(context).isAlarmActiveFlag();
      long currentTimestamp = ApplicationUtility.getCurrentTimestamp();

      if (AlarmActiveFlag) {
        dbHelper = DBHelper.getInstance(context);
        isMsgIdExist = dbHelper.getNotificationLogByMsgId(nDTO.getMessageId(), nDTO.isLiveNotification());
      }
      //Temp
      //if(true) {
      if (!isMsgIdExist) {
        Log.d(TAG, "NotificationDTO:" + nDTO);
        if (nDTO.getChannelId() != null && nameByChannelId.get(nDTO.getChannelId()) == null) {
          nameByChannelId.put(nDTO.getChannelId(), nDTO.getChannelName());
        }
        if (nDTO.getChannelId() != null && nameByChannelIdPref.getString(nDTO.getChannelId()) != null) {
          nameByChannelIdPref.saveString(nDTO.getChannelId(), nDTO.getChannelName());
        }

        String appName = getAppName(context);
        NotificationManager notificationManager = getNotificationManager();
        // only run this code if the device is running 23 or better
        if (Build.VERSION.SDK_INT >= 23) {
          StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
          if (activeNotifications.length > 0) {
            // Wait until notification with sound is notified
            if (nDTO.isReacall()) {
              if (!isRecallMessageExist(activeNotifications))
                return;
            }
            try {
              Thread.sleep(waitMillSecond);
            } catch (Exception e) {
            }
            NotificationManagerCompat.from(context).cancelAll();

            //If Single Notification and recall come of that
            if (nDTO.isReacall() && activeNotifications.length == 1) {
              if (!isInbox(activeNotifications[0]))
                return;
            }

            if (Build.VERSION.SDK_INT == 23)
              sendNotificationFor23API(activeNotifications);
            else
              sendNotificationAbove23API(activeNotifications);
          } else {
            if (!nDTO.isReacall()) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificationAbove26API(context, nDTO);
              } else {
                notifyBasicNotification(context, nDTO);
              }
            }
          }
        } else {
          if (nDTO.isReacall())
            notificationManager.cancel(nDTO.getRecalledMessageId());
          else
            notifyBasicNotification(context, nDTO);
        }
        // Insert data into NotificationMaster
        synchronized (this) {
          if (AlarmActiveFlag) {
            dbHelper.insertNotificationLog(nDTO.getMessageId(), nDTO.isLiveNotification());
            try {
              sharedPreferencesTask.saveInt(SharedPrefConstants.LAST_MSGID_FROM_NOTIFICATION, nDTO.getMessageId());
            } catch (Exception e) {
              Log.e(TAG, " Exception in sharedPreferencesTask " + e.getMessage());
              e.printStackTrace();
            }
          }
        }
        // Log Notification received in Firebase DB
        WebServiceCall.sendNotificationLog(context, pushType, nDTO);
        //AlarmSetupReceiver.setAlarm(context);
      } else {
        Log.d(TAG, "sendStackNotification Record exist in NotificationMaster table :");
      }
    } catch (Exception e) {
      Log.e(TAG, " Exception in sendStackNotification " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println(TAG + "\tSuccesfully completed sendNotification for :" + nDTO);
    Log.d(TAG, "Succesfully completed sendNotification for :" + nDTO);
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  private boolean isRecallMessageExist(StatusBarNotification[] activeNotifications) {
    for (StatusBarNotification sbn : activeNotifications) {
      String sbnMsgIds = getMessageId(sbn.getNotification().extras);
      if (!ApplicationUtility.isStrNullOrEmpty(sbnMsgIds)) {
        List<String> msgIdList = Arrays.asList(sbnMsgIds.split(","));
        if (msgIdList.contains("" + nDTO.getRecalledMessageId()))
          return true;
      }
    }
    return false;
  }

  private String getMessageId(Bundle bundle) {
    if (bundle != null)
      return bundle.getString(MESSAGE_ID);
    return "";
  }

  private void notifyBasicNotification(Context context, NotificationDTO notificationDTO) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      .setContentTitle(notificationDTO.getChannelName())
      .setTicker(notificationDTO.getChannelName())
      .setContentText(notificationDTO.getNotificationTitle(context))
      .setSmallIcon(getSmallIcon(context))
      .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationDTO.getNotificationTitle(context)))
      .setContentIntent(getMainActivityPendingIntent(context))
      .setLargeIcon(ApplicationUtility.getSenderImage(notificationDTO.getChannelId(), context))
      //.setNumber(1)
      .setDefaults(Notification.DEFAULT_VIBRATE)// For single Notifications vibration will be there, for grouped Notifications vibrations is removed
      ;
    setDefaultNotificationProperty(builder, true, "" + notificationDTO.getMessageId());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder.setCategory(notificationDTO.getChannelId());
    }
    NotificationManager notificationManager = getNotificationManager();
    notificationManager.notify(notificationDTO.getMessageId(), builder.build());
  }

  private void setDefaultNotificationProperty(NotificationCompat.Builder builder, boolean withSound, String messageId) {
    if (nDTO.isReacall())
      withSound = false;
    builder.setDefaults(Notification.DEFAULT_LIGHTS)
      //.setDefaults(Notification.DEFAULT_VIBRATE) // Removed to avoid continue vibration for multiple Notifications
      .setShowWhen(true)
      .setWhen(System.currentTimeMillis())
      .setAutoCancel(true);

    if (!ApplicationUtility.isStrNullOrEmpty(messageId)) {
      Bundle bundle = builder.getExtras();
      if (bundle == null)
        bundle = new Bundle();
      bundle.putString(MESSAGE_ID, messageId);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      builder.setColor(color);

    synchronized (AKonnectNotificationManager.class) {
      boolean toSetSound = true;
      if (previousNotificationTime != null) {
        Date currentTime = new Date();
        if ((currentTime.getTime() - previousNotificationTime.getTime()) < lDurationBtwTwoNotiForSound) {
          toSetSound = false;
          waitMillSecond = 0;
        }
      }
      if (toSetSound && withSound) {
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setDefaults(Notification.DEFAULT_ALL);
        previousNotificationTime = new Date();
        waitMillSecond = 1 * 1000;
      }
    }
  }

  private NotificationChannel getNotificationChannel(NotificationDTO notificationDTO, boolean withSound) {
    if (nDTO.isReacall())
      withSound = false;
    NotificationChannel notificationChannel = null;
    NotificationManager notificationManager = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      synchronized (AKonnectNotificationManager.class) {
        notificationManager = getNotificationManager();
        AudioAttributes att = new AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
          .build();

        boolean toSetSound = true;
        if (previousNotificationTime != null) {
          Date currentTime = new Date();
          if ((currentTime.getTime() - previousNotificationTime.getTime()) < lDurationBtwTwoNotiForSound) {
            toSetSound = false;
            waitMillSecond = 0;
          }
        }
        if (toSetSound && withSound) {
          previousNotificationTime = new Date();
          waitMillSecond = 1 * 1000;
          if (notificationManager.getNotificationChannel(notificationDTO.getChannelId()) != null) {
            notificationChannel = notificationManager.getNotificationChannel(notificationDTO.getChannelId());
          } else {
            notificationChannel = new NotificationChannel(notificationDTO.getChannelId(),
              nameByChannelId.get(notificationDTO.getChannelId()), NotificationManager.IMPORTANCE_HIGH);
          }
          if (notificationChannel != null && notificationChannel.getGroup() == null) {
            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(NOTIFICATION_CHANNEL_GROUP_AKONNECT_ID, NOTIFICATION_CHANNEL_GROUP_AKONNECT_DESC);
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);
            notificationChannel.setGroup(NOTIFICATION_CHANNEL_GROUP_AKONNECT_ID);
          }
          notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), att);
          //Moved below code to Single Notification builder to avoid continue vibration for multiple Notifications
                    /*notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(vibrate);*/
        } else {
          if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT) != null) {
            notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT);
            if (notificationChannel.getImportance() > NotificationManager.IMPORTANCE_LOW) {
              notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT);
              notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT,
                NOTIFICATION_CHANNEL_DESC_SILENT, NotificationManager.IMPORTANCE_LOW);
            }
          } else {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT,
              NOTIFICATION_CHANNEL_DESC_SILENT, NotificationManager.IMPORTANCE_LOW);
          }

          if (notificationChannel != null && notificationChannel.getGroup() == null) {
            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(NOTIFICATION_CHANNEL_GROUP_SYSTEM_ID, NOTIFICATION_CHANNEL_GROUP_SYSTEM_DESC);
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);
            notificationChannel.setGroup(NOTIFICATION_CHANNEL_GROUP_SYSTEM_ID);
          }
          notificationChannel.setSound(null, null);
          Log.d(TAG, "getNotificationChannel  Inside Else setting sound as NULL --> ");
        }

        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.TRANSPARENT);
//        notificationChannel.setLightColor(Color.RED);
        //notificationChannel.setShowBadge(true);
        Log.d(TAG, "getNotificationChannel  toSetSound --> " + toSetSound + ", withSound --> " + withSound);
      }
    }
    return notificationChannel;
  }

  private static String getAppName(Context context) {
    // Attempt to determine app name via package manager
    return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
  }

  private PendingIntent getMainActivityPendingIntent(Context context) {
    System.out.println("################# Inside AKonnectNotificationManager getMainActivityPendingIntent --> Ends");
    // Get launcher activity intent
    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getApplicationContext().getPackageName());

    // Make sure to update the activity if it exists
    launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

    System.out.println("################# Inside AKonnectNotificationManager getMainActivityPendingIntent --> Ends");
    // Convert intent into pending intent
    return PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE);

  }

  private NotificationManager getNotificationManager() {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    return notificationManager;
  }

  void sendNotificationAbove23API(StatusBarNotification[] activeNotifications) {
    if (Build.VERSION.SDK_INT >= 23) {
      StringBuilder grpMsg = new StringBuilder();
      int totalMessages = 0;
      NotificationManager notificationManager = getNotificationManager();
      Map<String, List<StatusBarNotification>> groupedNotification = new HashMap<String, List<StatusBarNotification>>();

      // step through all the active StatusBarNotifications and load groupedNotification
      for (StatusBarNotification sbn : activeNotifications) {
        String category = (String) sbn.getNotification().category;
        if (!group.equals(category)) {
          List<StatusBarNotification> notifications = groupedNotification.get(category);
          if (notifications == null) {
            notifications = new ArrayList<StatusBarNotification>();
            groupedNotification.put(category, notifications);
          }
          notifications.add(sbn);
        }
      }
      Log.d(TAG, "groupedNotification: " + groupedNotification);

      int k = 40;
      boolean isNotificationAdded = false;

      Set<Map.Entry<String, List<StatusBarNotification>>> entrySet = groupedNotification.entrySet();
      int totalGroup = entrySet.size();
      for (Map.Entry<String, List<StatusBarNotification>> entry : entrySet) {
        String category = entry.getKey();
        List<StatusBarNotification> notifications = entry.getValue();
        List<String> lines = new ArrayList();
        String messageId = "";
        boolean notifyInbox = true;
        for (StatusBarNotification sbn : notifications) {
          String currMsgId = sbn.getNotification().extras.getString(MESSAGE_ID);
          List<String> currMsgIdList = new ArrayList<>(Arrays.asList(currMsgId.split(",")));
          Log.d(TAG, "currMsgId: " + currMsgId + ", messagaeId: " + messageId);
          if (isInbox(sbn)) {
            CharSequence[] charSequences = (CharSequence[]) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT_LINES);
            if (nDTO.isReacall() && category.equalsIgnoreCase(nDTO.getChannelId())) {
              charSequences = getTitlesAfterRecallUpdate(charSequences, currMsgIdList);
            }
            //String inboxtTitle = (String) activeSbn.getNotification().extras.get(NotificationCompat.EXTRA_TITLE);
            for (CharSequence line : charSequences) {
              lines.add(line.toString());
            }
            Log.d(TAG, "Lines :$lines");
          } else {
            String stackNotificationLine = (String) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT);
            if (nDTO.isReacall() && category.equalsIgnoreCase(nDTO.getChannelId())) {
              if (currMsgId != null && currMsgId.equalsIgnoreCase(String.valueOf(nDTO.getRecalledMessageId()))) {
                if (stackNotificationLine != null && isAnyEqualTo(stackNotificationLine))
                  notifyInbox = false;
                //stackNotificationLine = RECALL_MSG;
              }
            }
            if (stackNotificationLine != null) {
              lines.add(stackNotificationLine);
            }
          }
          messageId += listToCommaSeparatedString(currMsgIdList);
        }

        //Add New Notification to Stack
        if (!nDTO.isReacall()) {
          if (category.equalsIgnoreCase(nDTO.getChannelId())) {
            if (!messageId.equals(""))
              messageId = messageId + "," + nDTO.getMessageId();
            else
              messageId = "" + nDTO.getMessageId();
            lines.add(nDTO.getNotificationTitle(context));
            isNotificationAdded = true;
          }
        }

        //Notify Inbox Notification
        if (notifyInbox && !lines.isEmpty()) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyInboxNotificatonAbove26(category, lines, k++, grpMsg, messageId);
          } else {
            notifyInboxNotificaton(category, lines, k++, grpMsg);
          }
          totalMessages = totalMessages + lines.size();
        } else
          totalGroup--;
      }
      if (!nDTO.isReacall() && !isNotificationAdded) {
        String channelId = nDTO.getChannelId();
        List<String> lines = new ArrayList(1);
        lines.add(nDTO.
          getNotificationTitle(context));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          notifyInboxNotificatonAbove26(channelId, lines, k++, grpMsg, "" + nDTO.getMessageId());
        } else {
          notifyInboxNotificaton(channelId, lines, k++, grpMsg);
        }
        totalMessages++;
        totalGroup++;
      }
      if (totalMessages > 0) {
        String totalMsg = new StringBuilder(String.valueOf(totalMessages))
          .append(" messages from ")
          .append(totalGroup)
          .append(" groups")
          .toString();
        //Notification.Builder groupBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          Notification.Builder groupBuilder;
          NotificationChannel notificationChannel = getNotificationChannel(nDTO, true);
          groupBuilder = new Notification.Builder(context, notificationChannel.getId());

          Notification.InboxStyle inbox = new Notification.InboxStyle();
          inbox.setSummaryText(totalMsg);
          inbox.addLine(grpMsg);

          groupBuilder.setContentTitle("AKonnect")
            .setContentText(grpMsg)
            .setGroupSummary(true)
            .setGroup(group)
            .setCategory(group)
            .setSmallIcon(getSmallIcon(context))
            .setLargeIcon(ApplicationUtility.getSenderImage(nDTO.getChannelId(), context))
            .setStyle(inbox)
            .setContentIntent(getMainActivityPendingIntent(context))
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setColor(color)
            //.setNumber(totalMessages)
            .setAutoCancel(true)
          ;
          //setDefaultNotificationPropertyAbove26(groupBuilder,true,notificationChannel);
          notificationManager.createNotificationChannel(notificationChannel);

          notificationManager.notify(AKONNECT_GROUP_ID, groupBuilder.build());

          Log.d(TAG, "sendNotificationAbove23API  mChannel --> " + notificationChannel.toString() + "\n groupBuilder --> " + groupBuilder.toString());
        } else {
          NotificationCompat.Builder groupBuilder;
          groupBuilder = new NotificationCompat.Builder(context);
          NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
          inbox.setSummaryText(totalMsg);
          inbox.addLine(grpMsg);

          groupBuilder.setContentTitle("AKonnect")
            .setContentText(grpMsg)
            .setGroupSummary(true)
            .setGroup(group)
            .setCategory(group)
            .setSmallIcon(getSmallIcon(context))
            .setLargeIcon(ApplicationUtility.getSenderImage(nDTO.getChannelId(), context))
            .setStyle(inbox)
            .setContentIntent(getMainActivityPendingIntent(context))
          //.setNumber(totalMessages)
          ;

          setDefaultNotificationProperty(groupBuilder, true, "-1");
          notificationManager.notify(AKONNECT_GROUP_ID, groupBuilder.build());
        }
      }
    }
  }

  private String listToCommaSeparatedString(List<String> strs) {
    StringBuilder out = new StringBuilder("");
    for (int i = 0; i < strs.size(); i++) {
      if (i == 0)
        out.append(strs.get(i));
      else
        out.append(",").append(strs.get(i));
    }
    return out.toString();
  }

  private boolean isAnyEqualTo(String title) {
    if (nDTO != null && nDTO.isLiveNotification())
      return true;
    return title.equalsIgnoreCase(nDTO.getEngTitle()) || title.equalsIgnoreCase(nDTO.getGujTitle()) || title.equalsIgnoreCase(nDTO.getHindiTitle());
  }

  private CharSequence[] getTitlesAfterRecallUpdate(CharSequence[] charSequences, List<String> msgIdList) {
    if (msgIdList != null && !msgIdList.isEmpty()) {
      int msgIdIndex = msgIdList.indexOf("" + nDTO.getRecalledMessageId());
      if (msgIdIndex > -1) {
        String msg = charSequences[msgIdIndex].toString();
        if (!ApplicationUtility.isStrNullOrEmpty(msg)) {
          if (isAnyEqualTo(msg)) {
            List<CharSequence> tempList = new ArrayList<>(Arrays.asList(charSequences));
            tempList.remove(msgIdIndex);
            msgIdList.remove(msgIdIndex);
            charSequences = tempList.toArray(new CharSequence[0]);
            //charSequences[msgIdIndex] = RECALL_MSG;
          }
        }
      }
    }
    return charSequences;
  }

  private int getMsgIdIndex(CharSequence[] charSequences, String msgId) {
    for (int i = 0; i < charSequences.length; i++) {
      if (charSequences[i].toString().equalsIgnoreCase(msgId))
        return i;
    }
    return -1;
  }

  private void notifyInboxNotificaton(String category, List<String> lines, int notId, StringBuilder grpMsg) {
    Log.d(TAG, "notifyInboxNotificaton  category --> " + category + "\n lines --> " + lines.toString() + "\n notId --> " + notId + "\n grpMsg --> " + grpMsg.toString());
    NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
    int lineCount = 0;
    String notificationLine = null;
    for (String line : lines) {
      inbox.addLine(line);
      notificationLine = line;
      lineCount++;
    }
    inbox.setSummaryText(lineCount + " Messages");
    StringBuilder strBuilder = new StringBuilder(getGroupName(category));
    if (lineCount > 1) {
      strBuilder.append(" (")
        .append(lineCount)
        .append(" messages")
        .append(")")
        .toString();
    }

    String notificationTitle = strBuilder.toString();
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      .setContentTitle(notificationTitle)
      .setTicker(notificationTitle)
      .setCategory(category)
      .setContentText(notificationLine)
      .setSmallIcon(getSmallIcon(context))
      .setLargeIcon(ApplicationUtility.getSenderImage(category, context))
      .setStyle(inbox)
      .setGroup(group)
      //.setNumber(lineCount)
      .setContentIntent(getMainActivityPendingIntent(context));
    setDefaultNotificationProperty(builder, false, "-1");
    Notification stackNotification = builder.build();
    NotificationManager notificationManager = getNotificationManager();
    notificationManager.notify(getAppName(context), notId, stackNotification);

    grpMsg.append(getGroupName(category))
      .append(" : ")
      /*.append(lineCount+ " Messages")
      .append(" :: ")
      .append(lines.toString())*/
      .append(notificationLine)
      .append("\n");
  }

  private void notifyInboxNotificatonAbove26(String category, List<String> lines, int notId, StringBuilder grpMsg, String messageIds) {
    Log.d(TAG, "notifyInboxNotificatonAbove26  category --> " + category + "\n lines --> " + lines.toString() + "\n notId --> " + notId + "\n grpMsg --> " + grpMsg.toString());
    Log.d(TAG, "!!!!!!!!!!! messageIds:" + messageIds);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Notification.Builder groupBuilder;
      NotificationManager notificationManager = getNotificationManager();
      NotificationChannel notificationChannel = getNotificationChannel(nDTO, false);
      groupBuilder = new Notification.Builder(context, notificationChannel.getId());

      Notification.InboxStyle inbox = new Notification.InboxStyle();
      int lineCount = 0;
      String notificationLine = null;
      for (String line : lines) {
        inbox.addLine(line);
        notificationLine = line;
        lineCount++;
      }
      inbox.setSummaryText(lineCount + " Messages");
      StringBuilder strBuilder = new StringBuilder(getGroupName(category));
      if (lineCount > 1) {
        strBuilder.append(" (")
          .append(lineCount)
          .append(" messages")
          .append(")")
          .toString();
      }

      String notificationTitle = strBuilder.toString();
      groupBuilder
        .setContentTitle(notificationTitle)
        .setTicker(notificationTitle)
        .setCategory(category)
        .setContentText(notificationLine)
        .setSmallIcon(getSmallIcon(context))
        .setLargeIcon(ApplicationUtility.getSenderImage(category, context))
        .setStyle(inbox)
        .setGroup(group)
        .setContentIntent(getMainActivityPendingIntent(context))
        .setWhen(System.currentTimeMillis())
        .setColor(color)
        .setAutoCancel(true)
        //.setNumber(lineCount)
        .setShowWhen(true);
      Bundle bundle = new Bundle();
      bundle.putString(MESSAGE_ID, messageIds);
      groupBuilder.setExtras(bundle);
      //setDefaultNotificationPropertyAbove26(groupBuilder, false,notificationChannel);
      notificationManager.createNotificationChannel(notificationChannel);
      Notification stackNotification = groupBuilder.build();
      notificationManager.notify(getAppName(context), notId, stackNotification);

      grpMsg.append(getGroupName(category))
        .append(" : ")
        /*.append(lineCount+ " Messages")
        .append(" :: ")
        .append(lines.toString())*/
        .append(notificationLine)
        .append("\n");
    }
  }

  private String getGroupName(String senderAliasId) {
    String groupName = senderAliasId;
    try {
      SharedPreferencesTask senderAliasMasterPref = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_SENDER_ALIAS_MASTER_PREF);
      if (senderAliasId != null && nameByChannelId.get(senderAliasId) != null) {
        groupName = nameByChannelId.get(senderAliasId);
        Log.d(TAG, "getGroupName INSIDE HASHMAP senderAliasId --> " + senderAliasId + "\n groupName --> " + groupName);
      }
      if (senderAliasId != null && senderAliasId.equals(groupName) && senderAliasMasterPref.getString(senderAliasId) != null) {
        groupName = senderAliasMasterPref.getString(senderAliasId);
        Log.d(TAG, "getGroupName INSIDE SHAREDPREF senderAliasId --> " + senderAliasId + "\n groupName --> " + groupName);
      }
    } catch (Exception e) {
      Log.e(TAG, "getGroupName  Exception --> " + e.getMessage());
      e.printStackTrace();
    }

    //Log.v(TAG,"getGroupName  senderAliasId --> "+senderAliasId +"\n groupName --> "+groupName);
    return groupName;
  }

  void sendNotificationFor23API(StatusBarNotification[] activeNotifications) {
    if (Build.VERSION.SDK_INT == 23) {
      NotificationManager notificationManager = getNotificationManager();
      NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
      int lineCount = 0;
      Set<String> uniqueGroupName = new HashSet<String>();
      String messageId = "";
      // step through all the active StatusBarNotifications and
      for (StatusBarNotification sbn : activeNotifications) {
        String currMsgId = sbn.getNotification().extras.getString(MESSAGE_ID);
        List<String> currMsgIdList = new ArrayList<>(Arrays.asList(currMsgId.split(",")));
        Log.d(TAG, "currMsgId:" + currMsgId + ", messagaeId:" + messageId);
        //Load lines for Notification (If Inbox add all, else get one line)
        if (isInbox(sbn)) {
          CharSequence[] charSequences = (CharSequence[]) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT_LINES);
          if (nDTO.isReacall()) {
            charSequences = getTitlesAfterRecallUpdate(charSequences, currMsgIdList);
          }
          for (CharSequence line : charSequences) {
            inbox.addLine(line);
            String[] contents = String.valueOf(line).split(":");
            if (contents.length > 0) {
              uniqueGroupName.add(contents[0]);
            }
            lineCount++;
          }
        } else {
          String title = (String) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TITLE);
          uniqueGroupName.add(title);
          String stackNotificationLine = (String) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT);
          if (nDTO.isReacall()) {
            if (currMsgId != null && currMsgId.equalsIgnoreCase(String.valueOf(nDTO.getRecalledMessageId()))) {
              if (stackNotificationLine != null && isAnyEqualTo(stackNotificationLine))
                stackNotificationLine = null;
                //stackNotificationLine = RECALL_MSG;
            }
          }
          if (stackNotificationLine != null) {
            inbox.addLine(title + ": " + stackNotificationLine);
            lineCount++;
          }
        }
        messageId += listToCommaSeparatedString(currMsgIdList);
      }
      if(!nDTO.isReacall()) {
        inbox.addLine(nDTO.getChannelName() + ": " + nDTO.getNotificationTitle(context));
        uniqueGroupName.add(nDTO.getChannelName());
        lineCount++;
      }
      if(lineCount > 0) {
        inbox.setSummaryText(lineCount + " New Messages from " + uniqueGroupName.size() + " groups");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
          .setContentTitle(lineCount + " New Messages from " + uniqueGroupName.size() + " groups")
          .setTicker(lineCount + " Messages from " + uniqueGroupName.size() + " groups")
          .setContentText(lineCount + " Messages from " + uniqueGroupName.size() + " groups")
          .setWhen(System.currentTimeMillis())
          .setSmallIcon(getSmallIcon(context))
          .setLargeIcon(ApplicationUtility.getSenderImage(null, context))
          .setStyle(inbox)
          //.setNumber(lineCount)
          .setContentIntent(getMainActivityPendingIntent(context));
        setDefaultNotificationProperty(builder, true, messageId);
        Notification stackNotification = builder.build();
        notificationManager.notify(getAppName(context), AKONNECT_NOT_ID, stackNotification);
      }
    }
  }

  boolean isInbox(StatusBarNotification sbn) {
    String template = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        template = (String) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEMPLATE);
    }
    return template != null && template.equalsIgnoreCase("android.app.Notification$InboxStyle");
  }

  void sendNotificationAbove26API(Context context, NotificationDTO notificationDTO) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager notificationManager = getNotificationManager();
      NotificationChannel notificationChannel = getNotificationChannel(notificationDTO, true);
      Notification.Builder builder;

      notificationChannel.enableVibration(true);
      notificationChannel.setVibrationPattern(vibrate); // setVibration
      builder = new Notification.Builder(context, notificationChannel.getId());

      Bundle bundle = builder.getExtras();
      Log.d(TAG, "bundle:" + bundle);
      if (bundle == null)
        bundle = new Bundle();
      bundle.putString(MESSAGE_ID, "" + notificationDTO.getMessageId());
      //NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      builder.setContentTitle(notificationDTO.getChannelName())
        .setTicker(notificationDTO.getChannelName())
        .setContentText(notificationDTO.getNotificationTitle(context))
        .setSmallIcon(getSmallIcon(context))
        .setStyle(new Notification.BigTextStyle().bigText(notificationDTO.getNotificationTitle(context)))
        .setContentIntent(getMainActivityPendingIntent(context))
        .setLargeIcon(ApplicationUtility.getSenderImage(notificationDTO.getChannelId(), context))
        .setWhen(System.currentTimeMillis())
        .setColor(color)
        .setAutoCancel(true)
        .setCategory(notificationDTO.getChannelId())
        .setShowWhen(true)
        .setExtras(bundle)
      //.setNumber(1)
      ;

      //setDefaultNotificationPropertyAbove26(builder,true,notificationChannel);
      notificationManager.createNotificationChannel(notificationChannel);
      notificationManager.notify(notificationDTO.getMessageId(), builder.build());
    }
  }

  public static int getSmallIcon(Context context) {
    Drawable d = context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
    return context.getResources().getIdentifier("notification_icon", "drawable", context.getPackageName());
  }


}
