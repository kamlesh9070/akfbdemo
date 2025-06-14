package org.dadabhagwan.AKonnect.constants;

/**
 * Created by coder2 on 25-Jun-2018.
 */

public interface SharedPrefConstants {


  String FILE_NAME_NOTIFICATION_LOG_PREF = "NotificationLog";
  String LATEST_MESSAGES_BY_MSGID_APIURL = "LatestMessagesByMsgIdApiUrl";  //Not Get
  String API_URL = "ApiUrl"; //Not Get
  String API_KEY = "ApiKey"; //Not Get
  String ACTIVATION_SRNO = "ActivationSrNo";  //Not Get
  String DEVICE_MODEL = "DeviceModel"; ///
  String DEVICE_OS = "DeviceOs"; ///
  String DEVICE_OS_VERSION = "OsVersion"; ///
  String APP_VERSION = "AppVersion"; ///
  String LAST_MSGID_FROM_INBOX = "LastMsgIdFromInbox";  // Not Get
  String SUBSCRIBER_ID = "SubscriberId"; ///
  String LAST_MSGID_FROM_NOTIFICATION = "LastMsgIdFromNotification"; // Not Get  Created New
  String DEVICE_MANUFACTURER_LIST = "DeviceManufacturerList";  // Not Get
  String REPEAT_ALARM_TIME_IN_MINUTES = "RepeatAlarmTimeInMinutes";
  String LAST_SEEN_TIMESTAMP = "LastSeenTimeStamp"; ///
  String ALARM_OFFSET_WINDOW_IN_SECONDS = "AlarmOffsetWindowInMinutes"; ///
  String ALARM_ACTIVE_FLAG = "AlarmActiveFlag"; ///
  String NEXT_ALARM_SCHEDULED_TIME = "NextAlarmScheduledTime";


  //App SharedPref
  String FILE_NAME_APP_MAIN_PREF = "FlutterSharedPreferences";
  String FLUTTER_CHANNELDETAILS = "flutter.ChannelDetails";
  String FLUTTER_USERPROFILE = "flutter.UserProfile";
  String FLUTTER_DEVICEDETAILS = "flutter.DeviceDetails";
  String FLUTTER_INITAPPRESPONSE = "flutter.InitAppResponse";
  String FLUTTER_USERREGDATA = "flutter.UserRegData";
  String FLUTTER_PROFILEHASH = "flutter.ProfileHash";
  String FLUTTER_TOKEN = "flutter.Token";
  String FLUTTER_MESSAGE_DELIVERY_URL = "message_delivery_url";
  String PREF_CHANNEL_IMAGE = "ChannelImage";
  String FILE_NAME_SENDER_ALIAS_MASTER_PREF = "SenderAliasMaster";

}
