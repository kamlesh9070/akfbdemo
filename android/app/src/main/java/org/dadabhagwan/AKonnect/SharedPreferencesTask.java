package org.dadabhagwan.AKonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.ChannelDetails;
import org.dadabhagwan.AKonnect.dto.DeviceDetail;
import org.dadabhagwan.AKonnect.dto.InitAppResponse;
import org.dadabhagwan.AKonnect.dto.UserProfile;
import org.dadabhagwan.AKonnect.dto.UserRegData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesTask {

  protected static final String TAG = "SharedPrefTask";
  String sharedPrefFileName;
  Context context;

  private static Map<String, SharedPreferencesTask> sharedPreferencesTaskMap = new HashMap<>();

  private static ArrayList<ChannelDetails> channelDetails;
  private static Map<String, ChannelDetails> channelById = new HashMap<>();
  private static UserProfile userProfile;
  private static DeviceDetail deviceDetail;
  private static InitAppResponse initAppResponse;
  private static UserRegData userRegData;
  public static SharedPreferencesTask getSharedPreferenceTask(Context context, String sharedPrefFileName) {
    SharedPreferencesTask sTask = sharedPreferencesTaskMap.get(sharedPrefFileName);
    if (sTask == null) {
      sTask = new SharedPreferencesTask(context, sharedPrefFileName);
      sharedPreferencesTaskMap.put(sharedPrefFileName, sTask);
    }
    return sTask;
  }

  public SharedPreferencesTask(Context context, String sharedPrefFileName) {
    this.sharedPrefFileName = sharedPrefFileName;
    this.context = context;
  }

  public void saveString(String key, String value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(key, value);
    editor.commit();
  }

  public String getString(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    String str = "";
    if (sharedPreferences.contains(key)) {
      str = sharedPreferences.getString(key, "");
    }
    return str;
  }

  public SharedPreferences getSharedPref() {
    return context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
  }

  public void saveInt(String key, int value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(key, value);
    editor.commit();
  }

  public int getInt(String key) {
    return getInt(key, 0);
  }

  public int getInt(String key, int defaultValue) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);

    if (sharedPreferences.contains(key)) {
      return sharedPreferences.getInt(key, defaultValue);
    } else {
      return defaultValue;
    }
  }

  public void saveLong(String key, long value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putLong(key, value);
    editor.commit();
  }

  public long getLong(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);

    if (sharedPreferences.contains(key)) {
      return sharedPreferences.getLong(key, 0);
    } else {
      return 0;
    }
  }

  public void saveBoolean(String key, boolean value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(key, value);
    editor.commit();
  }

  public boolean getBoolean(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    if (sharedPreferences.contains(key)) {
      return sharedPreferences.getBoolean(key, false);
    } else {
      return true;
    }
  }

  public void clearPreferences(Context context) {
    context.getSharedPreferences(sharedPrefFileName, 0).edit().clear().commit();
  }


  public void removePreference(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(key);
    editor.commit();
  }


  public static ChannelDetails getChannelDetails(String channelId, Context context) {
    loadChannelDetailsList(context);
    if (channelDetails!= null && channelById.isEmpty()) {
      for (ChannelDetails channelDetails : channelDetails) {
        channelById.put(channelDetails.getChannelId(), channelDetails);
      }
    }
    return channelById.get(channelId);
  }

  public static void loadChannelDetailsList(Context context) {
    try {
      if (channelDetails == null) {
        SharedPreferencesTask sharedPreferencesTask = SharedPreferencesTask.getSharedPreferenceTask(context, SharedPrefConstants.FILE_NAME_APP_MAIN_PREF);
        String channelsStr = sharedPreferencesTask.getString(SharedPrefConstants.FLUTTER_CHANNELDETAILS);
        if (!ApplicationUtility.isStrNullOrEmpty(channelsStr)) {
          Type channelListType = new TypeToken<ArrayList<ChannelDetails>>() {
          }.getType();
          channelDetails = new Gson().fromJson(channelsStr, channelListType);
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "Error while getting Channel Details", e);
    }
  }

  public static InitAppResponse getInitAppResponse(Context context) {
    return getDTO(context, SharedPrefConstants.FLUTTER_INITAPPRESPONSE, initAppResponse, InitAppResponse.class);
  }

  public static UserProfile getUserProfile(Context context) {
    return getDTO(context, SharedPrefConstants.FLUTTER_USERPROFILE, userProfile, UserProfile.class);
  }

  public static DeviceDetail getDeviceDetail(Context context) {
    return getDTO(context, SharedPrefConstants.FLUTTER_DEVICEDETAILS, deviceDetail, DeviceDetail.class);
  }

  public static UserRegData getUserRegData(Context context) {
    return getDTO(context, SharedPrefConstants.FLUTTER_USERREGDATA, userRegData, UserRegData.class);
  }

  public static <T> T getDTO(Context context, String prefName, T dto, Class<T> tClass) {
    try {
      if (dto == null) {
        SharedPreferencesTask sharedPreferencesTask = SharedPreferencesTask.getSharedPreferenceTask(context, SharedPrefConstants.FILE_NAME_APP_MAIN_PREF);
        String dtoStr = sharedPreferencesTask.getString(prefName);
        if (!ApplicationUtility.isStrNullOrEmpty(dtoStr)) {
          dto = new Gson().fromJson(dtoStr, tClass);
        }
      }
      return dto;
    } catch (Exception e) {
      Log.e(TAG, "Error while getting DTO Details. prefName:" + prefName, e);
    }
    return null;
  }

  public static void saveInitAppResponse(Context context) {
    if(initAppResponse != null)
      saveDTO(context, SharedPrefConstants.FLUTTER_INITAPPRESPONSE, initAppResponse);
  }

  public static <T> void saveDTO(Context context, String prefName, T dto) {
    SharedPreferencesTask sharedPreferencesTask = SharedPreferencesTask.getSharedPreferenceTask(context, SharedPrefConstants.FILE_NAME_APP_MAIN_PREF);
    String jsonStr = new Gson().toJson(dto);
    sharedPreferencesTask.saveString(prefName, jsonStr);
  }

  public static String getProfileHash(Context context) {
    return getFlutterSharedPrefString(context, SharedPrefConstants.FLUTTER_PROFILEHASH);
  }

  public static String getToken(Context context) {
    return getFlutterSharedPrefString(context, SharedPrefConstants.FLUTTER_TOKEN);
  }

  public static String getFlutterSharedPrefString(Context context, String prefName) {
    SharedPreferencesTask sharedPreferencesTask = SharedPreferencesTask.getSharedPreferenceTask(context, SharedPrefConstants.FILE_NAME_APP_MAIN_PREF);
    return sharedPreferencesTask.getString(prefName);
  }

  public static void saveFlutterSharedPrefString(Context context, String prefName, String value) {
    SharedPreferencesTask sharedPreferencesTask = SharedPreferencesTask.getSharedPreferenceTask(context, SharedPrefConstants.FILE_NAME_APP_MAIN_PREF);
    sharedPreferencesTask.saveString(prefName, value);
  }


}
