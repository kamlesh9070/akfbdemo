package org.dadabhagwan.AKonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.constants.WSConstant;
import org.dadabhagwan.AKonnect.dbo.AKDBHelper;
import org.dadabhagwan.AKonnect.dbo.DBHelper;
import org.dadabhagwan.AKonnect.dto.DeviceDetail;
import org.dadabhagwan.AKonnect.dto.InitAppResponse;
import org.dadabhagwan.AKonnect.dto.NotificationDTO;
import org.dadabhagwan.AKonnect.dto.NotificationLog;
import org.dadabhagwan.AKonnect.dto.PullNotificationDTO;
import org.dadabhagwan.AKonnect.dto.UserProfile;
import org.dadabhagwan.AKonnect.dto.UserRegData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coder2 on 25-Jun-2018.
 */

public class WebServiceCall {

  protected static final String TAG = "AKonnect[WSCall]";

  private static String getLatestMessagesByMsgIdApiUrl(Context context) {
    SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
    return sharedPreferencesTask.getString(SharedPrefConstants.LATEST_MESSAGES_BY_MSGID_APIURL);
  }

  private static String getApiUrl(Context context) {
        /*SharedPreferences sharedPreferences =
                ApplicationUtility.getAkonnectSharedPreferences(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
        return sharedPreferences.getString(SharedPrefConstants.API_URL,null);*/
    SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
    return sharedPreferencesTask.getString(SharedPrefConstants.API_URL);
  }

  public static void sendNotificationLog(Context context, String pushType, NotificationDTO notificationDTO) {

    try {
      //SharedPreferences sharedPreferences = ApplicationUtility.getAkonnectSharedPreferences(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      // if (sharedPreferences != null) {
                /*Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d(TAG, "NOTIFICATION_LOG_PREF Shared values" + entry.getKey() + ": " + entry.getValue().toString());
                }*/
      //String ApiUrl = sharedPreferences.getString(String.valueOf("ApiUrl"), null);
      String ApiUrl = getLogUrl(context);
      int ActivationSrNo = 0;

      if(!ApplicationUtility.isStrNullOrEmpty(ApiUrl)) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setFieldsFromDTO(SharedPreferencesTask.getUserRegData(context));
        notificationLog.setFieldsFromDTO(SharedPreferencesTask.getDeviceDetail(context));
        notificationLog.setFieldsFromNDTO(pushType, notificationDTO);
        notificationLog.setFieldsFromUserProfile(SharedPreferencesTask.getUserProfile(context));
        notificationLog.setNetwork(NetworkUtils.getNetworkState(context));
        HttpPostAsyncTask task = new HttpPostAsyncTask(new Gson().toJson(notificationLog), null);
        task.execute(ApiUrl);
      }

      //}
    } catch (Exception e) {
      Log.d(TAG, "sendNotificationLog Exception : " + e.getMessage());
    }

  }


  public static String getLogUrl(Context context) {
    InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
    if(initAppResponse != null && initAppResponse.getStatisticsUrl() != null && !initAppResponse.getStatisticsUrl().isEmpty())
      return initAppResponse.getStatisticsUrl().get(SharedPrefConstants.FLUTTER_MESSAGE_DELIVERY_URL);
    return null;
  }

  public static void fetchMsgFromServer(Context context, AsyncResponseListner asyncResponseListner) {
    Log.d(TAG, "Inside fetchMsgFromServer");
    try {
      int lastMsgIdFromInbox = 0;
      int maxMsgId = 0;
      int lastNotificationId = 0;
      PullNotificationDTO pullNotificationDTO = new PullNotificationDTO();
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
//      lastNotificationId = sharedPreferencesTask.getInt(SharedPrefConstants.LAST_MSGID_FROM_NOTIFICATION);
      lastNotificationId = DBHelper.getInstance(context).getMaxMsgIdFromNotificationMaster();
      lastMsgIdFromInbox = AKDBHelper.getInstance(context).getMaxMsgIdFromMessageMaster();
      maxMsgId = Math.max(lastNotificationId, lastMsgIdFromInbox);

      //Temp
//      maxMsgId = 23553;
      Log.d(TAG, "lastNotificationId : " + lastNotificationId + ", lastMsgIdFromInbox: " + lastMsgIdFromInbox);
      UserRegData userRegData = SharedPreferencesTask.getUserRegData(context);
      UserProfile userProfile = SharedPreferencesTask.getUserProfile(context);
      InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
      Log.d(TAG, "initAppResponse : " + initAppResponse);

      if(maxMsgId > 0 && userRegData != null && initAppResponse != null) {

        String token = SharedPreferencesTask.getToken(context);
        if(!ApplicationUtility.isStrNullOrEmpty(token)) {
          pullNotificationDTO.setToken(SharedPreferencesTask.getToken(context));
        } else {
          pullNotificationDTO.setDevice(userRegData.getDevice());
          pullNotificationDTO.setSubscriber(userProfile.getSubscriber());
        }

        pullNotificationDTO.setLastMessageId(maxMsgId);
        Log.d(TAG, "userProfile:" + userProfile);
        String profileHash = SharedPreferencesTask.getProfileHash(context);
        if(!ApplicationUtility.isStrNullOrEmpty(profileHash))
          pullNotificationDTO.setProfileHash(profileHash);

        if(userProfile != null && userProfile.getSenderChannelList() != null && !userProfile.getSenderChannelList().isEmpty()) {
          pullNotificationDTO.setIsCoordinator(1);
          pullNotificationDTO.setProfileHash("");
        }
        else {
          pullNotificationDTO.setIsCoordinator(0);
        }

        String pullUrl = initAppResponse.getPull_notifications_url();
        if(!ApplicationUtility.isStrNullOrEmpty(pullUrl)) {
          HttpPostAsyncTask task = new HttpPostAsyncTask(new Gson().toJson(pullNotificationDTO), "", asyncResponseListner);
          task.execute(pullUrl);
        }
      }
    } catch (Exception e) {
      Log.d(TAG, "sendNotificationLog Exception : " + e.getMessage());
      e.getStackTrace();
      e.printStackTrace();
    }

  }

}
