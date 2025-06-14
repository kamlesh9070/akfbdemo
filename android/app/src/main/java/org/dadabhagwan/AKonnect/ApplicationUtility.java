package org.dadabhagwan.AKonnect;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.constants.WSConstant;
import org.dadabhagwan.AKonnect.dto.ChannelDetails;
import org.dadabhagwan.AKonnect.dto.NotificationDTO;
import org.dadabhagwan.AKonnect.dto.NotificationPullRes;
import org.dadabhagwan.AKonnect.dto.PullNotificationDTO;
import org.dadabhagwan.AKonnect.dto.ServerResponseDTO;
import org.dadabhagwan.AKonnect.utils.WSUtils;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ApplicationUtility {

  protected static final String TAG = "AKonnect[AppUtility]";

  public static boolean isPopupOpen = false;

  public static final String PREF_FILE_NAME = "SenderImage";

//  private static final String DEFAULT_LOGO = "amlogo";
private static final String DEFAULT_LOGO = "ic_launcher";

  public static final String DEVICE_XIAOMI = "xiaomi";
  public static final String DEVICE_HUAWEI = "HUAWEI";
  public static final String DEVICE_ONEPLUS = "OnePlus";
  //public  HttpPostAsyncTask asyncTask =new HttpPostAsyncTask(null,null);
  protected static final int REQUEST_CHECK_SETTINGS = 0x1;


  public static boolean isDeviceManufacturerSupported(Context context) {
    try {
      String manufacturer = Build.MANUFACTURER;
      String manufacturerList = "";
      SharedPreferences sharedPreferences = ApplicationUtility.getAkonnectSharedPreferences(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);

      if (sharedPreferences != null) {
        manufacturerList = sharedPreferences.getString(SharedPrefConstants.DEVICE_MANUFACTURER_LIST, "");
      }
      Log.d(TAG, "Current Device: " + manufacturer + " , Device manufacturer List from Server is : " + manufacturerList);

      if (manufacturer != null && manufacturerList != "" && manufacturerList.toLowerCase().contains(manufacturer.toLowerCase())) {
        return true;
      }
    } catch (Exception e) {
      Log.d(TAG, "Exception in isDeviceManufacturerSupported: " + e.getMessage());
      e.printStackTrace();
    }

    return false;
  }

  public static SharedPreferences getAkonnectSharedPreferences(Context context, String sharedPref) {
    return context.getSharedPreferences(sharedPref, MODE_PRIVATE);
  }

  public static Bitmap getSenderImage(String channelId, Context context) {
    Bitmap channelImg = null;
    try {
      if (!isStrNullOrEmpty(channelId)) {
        ChannelDetails channel = SharedPreferencesTask.getChannelDetails(channelId, context);
        if (channel != null && !isStrNullOrEmpty(channel.getAvatarUrl())) {
          SharedPreferencesTask sharedPreferences = SharedPreferencesTask.getSharedPreferenceTask(context, SharedPrefConstants.PREF_CHANNEL_IMAGE);
          String bitMapStr = sharedPreferences.getString(channel.getAvatarUrl());
          if (isStrNullOrEmpty(bitMapStr)) {
            channelImg = ImageUtility.getDownloadedImage(channel.getAvatarUrl());
            if (channelImg != null) {
              bitMapStr = ImageUtility.bitMapToString(channelImg);
              sharedPreferences.saveString(channel.getAvatarUrl(), bitMapStr);
            }
          } else
            channelImg = ImageUtility.stringToBitMap(bitMapStr);
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "Error while getting sender Image, channelId:"+ channelId + "\te:" + e.getMessage(), e);
    }
    if (channelImg == null)
      channelImg = getDefaultChannelImg(context);
    return channelImg;
  }

  public static Bitmap getDefaultChannelImg(Context context) {
    return BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(DEFAULT_LOGO, "mipmap", context.getPackageName()));
  }

  public static void showMessageOKCancel(Activity activity, String title, String message, DialogInterface.OnClickListener clickListener) {
    if (!isPopupOpen) {
      android.app.AlertDialog.Builder builder;
      builder = new android.app.AlertDialog.Builder(activity, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
      builder.setMessage(Html.fromHtml(message))
        .setPositiveButton("Ok", clickListener)
        .setNegativeButton("Cancel", clickListener);
      if (title != null && title.trim() != "") {
        builder.setTitle(Html.fromHtml(title));
      }
      Dialog dialog = builder.create();
      dialog.setCancelable(false);
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
      synchronized (ApplicationUtility.class) {
        isPopupOpen = true;
      }
    }
  }

  public static void requestStoragePermission(final Activity activity) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ViewGroup viewgroup = (ViewGroup) activity.findViewById(android.R.id.content).getParent();
        View view = viewgroup.getChildAt(0);
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
          viewgroup.getChildAt(i);
        }

        PermissionProcessor permissionProcessor = new PermissionProcessor(activity, view);
        permissionProcessor.setPermissionGrantListener(new PermissionGrantListener() {
          public void OnGranted() {
            System.out.println("Inside on granted");
          }
        });

        permissionProcessor.askForPermissionExternalStorage();
      }
    });
  }


  public static boolean isAppOnForeground(Context context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return isAppOnForegroundFor21AndAbove(context);
      } else {
        return isAppOnForegroundForBelow21(context);
      }
    } catch (Exception e) {
      System.out.println("Exception while getting forground app : Message:" + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  private static boolean isAppOnForegroundForBelow21(Context context) {
    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
    String topPackageName = foregroundTaskInfo.topActivity.getPackageName();
    if (context.getPackageName().equals(topPackageName)) {
      return true;
    }
    return false;
  }

  private static boolean isAppOnForegroundFor21AndAbove(Context context) {
    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
    if (appProcesses == null) {
      return false;
    }
    final String packageName = context.getPackageName();
    for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
      if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName) && appProcess.importanceReasonCode == 0) {
        return true;
      }
    }
    return false;
  }

  public static boolean isOnline(Context context) {
    ConnectivityManager connectivityManager;
    boolean connected = false;
    try {
      connectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager
        .getActiveNetworkInfo();
      connected = networkInfo != null && networkInfo.isConnected();
      if (networkInfo != null)
        Log.d(TAG, " isOnline networkInfo" + networkInfo.toString());
    } catch (Exception e) {
      Log.d(TAG, e.toString());
      e.printStackTrace();
    }
    return connected;
  }

  public static void handlePullNotificationRes(Context context, String request, String response) {
    try {
      Log.d(TAG, "handlePullNotificationRes response:" + response);
      PullNotificationDTO pullNotificationDTO = null;
      if(!ApplicationUtility.isStrNullOrEmpty(request)) {
        try {
          pullNotificationDTO = new Gson().fromJson(request, PullNotificationDTO.class);
        } catch (Exception e) {
          Log.e(TAG, "processFinish output Exception:: " + e.getMessage());
          e.printStackTrace();
        }
      }
      ServerResponseDTO<NotificationPullRes> serverResponseDTO = WSUtils.getResponse(response, NotificationPullRes.class);
      if (serverResponseDTO != null && serverResponseDTO.isSuccess()) {
        NotificationPullRes notificationPullRes = serverResponseDTO.getData();
        Log.d(TAG, "handlePullNotificationRes notificationPullRes:" + notificationPullRes);
        if (notificationPullRes != null && notificationPullRes.isProcessFlag()) {
          List<NotificationDTO> notificationDTOs = notificationPullRes.getNotificationDTOList();
          if (notificationDTOs != null && !notificationDTOs.isEmpty())
            ApplicationUtility.generateNotificationsForFetchMessages(context, pullNotificationDTO, serverResponseDTO.getData().getNotificationDTOList());
          if(!isStrNullOrEmpty(notificationPullRes.getProfile_hash()))
            SharedPreferencesTask.saveFlutterSharedPrefString(context, SharedPrefConstants.FLUTTER_PROFILEHASH, notificationPullRes.getProfile_hash());
          if(!isStrNullOrEmpty(notificationPullRes.getToken()))
            SharedPreferencesTask.saveFlutterSharedPrefString(context, SharedPrefConstants.FLUTTER_TOKEN, notificationPullRes.getToken());
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "processFinish output Exception:: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static boolean generateNotificationsForFetchMessages(Context context, PullNotificationDTO pullNotificationDTO, List<NotificationDTO> notificationDTOList) {
    boolean isSucceeded = true;
    try {
      for (NotificationDTO notificationDTO : notificationDTOList) {
        notificationDTO.setPullNotificationDTO(pullNotificationDTO);
        new AKonnectNotificationManager(context, notificationDTO).sendStackNotification(WSConstant.PUSHTYPE_PULL); // 3 - Notifications fetched from server
      }
    } catch (Exception e) {
      isSucceeded = false;
      Log.d(TAG, "Exception in generateNotificationsForFetchMessages --> " + e.toString());
      e.printStackTrace();
    }
    return isSucceeded;
  }

  public static long getCurrentTimestamp() {
    long currentTimestamp = 0;
    try {
      currentTimestamp = System.currentTimeMillis();
    } catch (Exception e) {
      Log.d(TAG, e.toString());
    }
    return currentTimestamp;
  }


/*    private static boolean isCurrentAppInBackground(Context context) {
        String topPackageName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        }
        if (context.getPackageName().equalsIgnoreCase(topPackageName))
            return true;
        return false;
    }*/






        /*public static void requestGPSSettings(final Activity activity) {
            try {

                    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                            .addApi(LocationServices.API).build();
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                        locationRequest.setInterval(2000);
                        locationRequest.setFastestInterval(500);
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                        builder.setAlwaysShow(true);
                        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                            @Override
                            public void onResult(LocationSettingsResult result) {
                                try {
                                    final Status status = result.getStatus();
                                    switch (status.getStatusCode()) {
                                        case LocationSettingsStatusCodes.SUCCESS:
                                            Log.i("", "All location settings are satisfied.");
                                            //Toast.makeText(activity.getApplication(), "GPS is already enable", Toast.LENGTH_SHORT).show();
                                            break;
                                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                            Log.i("", "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
                                            try {
                                                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                                            } catch (IntentSender.SendIntentException e) {
                                                Log.e("Applicationsett", e.toString());
                                            }
                                            break;
                                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                            Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
                                            Toast.makeText(activity.getApplication(), "Location settings are inadequate, and cannot be fixed here", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error on enable GPS Result");
                                    e.printStackTrace();

                                }
                            }
                        });
                        askCount++;
                    }

            } catch (Exception e) {
                Log.e(TAG, "Error on enable GPS");
                e.printStackTrace();

            }
        }*/

  public static boolean isStrNullOrEmpty(String str) {
    return str == null || str.isEmpty() || str.equalsIgnoreCase("null");
  }
}
