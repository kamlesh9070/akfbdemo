package org.dadabhagwan.AKonnect;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionProcessor {
  private Activity activity;
  private View view;
  private PermissionGrantListener permissionGrantListener;
  public static final int REQUEST_EXTERNAL_STORAGE = 101;

  public PermissionProcessor(Activity activity, View view) {
    this.activity = activity;
    this.view = view;
  }

  public void setPermissionGrantListener(PermissionGrantListener permissionGrantListener) {
    this.permissionGrantListener = permissionGrantListener;
  }

  //Ask for read-write external storage permission

  public void askForPermissionExternalStorage() {
    try {
      if (ContextCompat.checkSelfPermission(activity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) { //permission yet to be granted

        getPermissionExternalStorage();
      } else { //permission already granted
        if (permissionGrantListener != null) {
          permissionGrantListener.OnGranted();
        }
      }
    } catch (Exception e) {
      System.out.println("Exception while asking permission, Exception:" + e.getMessage());
      e.printStackTrace();
      try {
        android.widget.Toast.makeText(activity.getApplicationContext(), "Issue while showing App permission", android.widget.Toast.LENGTH_LONG);
      } catch (Exception e1) {
        e1.getMessage();
      }
    }
  }

  //Request and get the permission for external storage

  public void getPermissionExternalStorage() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
      Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      if (!ApplicationUtility.isPopupOpen)
        ActivityCompat.requestPermissions(activity,
          new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
          REQUEST_EXTERNAL_STORAGE);
    } else {
      DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
          synchronized (ApplicationUtility.class) {
            ApplicationUtility.isPopupOpen = false;
          }
          switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
              openAppSetting();
              break;
          }
        }
      };
      ApplicationUtility.showMessageOKCancel(activity, Messages.neverAskAgainMsgTitle, Messages.neverAskAgainMsg, okListener);
    }
  }


  void openAppSetting() {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
    intent.setData(uri);
    activity.startActivity(intent);
  }


}
