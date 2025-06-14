package org.dadabhagwan.AKonnect;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionUtils {

  Context context;
  Activity current_activity;

  PermissionResultCallback permissionResultCallback;

  ArrayList<String> permission_list = new ArrayList<String>();
  ArrayList<String> listPermissionsNeeded = new ArrayList<String>();
  String dialog_content = "";
  int req_code;

  //View view;

  public PermissionUtils(Context context, Activity activity, PermissionResultCallback permissionResultCallback) {
    this.context = context;
    this.current_activity = activity;
    this.permissionResultCallback = permissionResultCallback;
    //this.view = view;
  }


  /**
   * Check the API Level & Permission
   *
   * @param permissions
   * @param dialog_content
   * @param request_code
   */

  public void check_permission(ArrayList<String> permissions, String dialog_content, int request_code) {
    this.permission_list = permissions;
    this.dialog_content = dialog_content;
    this.req_code = request_code;

    if (Build.VERSION.SDK_INT >= 23) {
      if (checkAndRequestPermissions(permissions, request_code)) {
        permissionResultCallback.PermissionGranted(request_code);
        Log.i("all permissions", "granted");
        Log.i("proceed", "to callback");
      }
    } else {
      permissionResultCallback.PermissionGranted(request_code);
      Log.i("all permissions", "granted");
      Log.i("proceed", "to callback");
    }

  }

  /**
   * Check and request the Permissions
   *
   * @param permissions
   * @param request_code
   * @return
   */

  private boolean checkAndRequestPermissions(ArrayList<String> permissions, final int request_code) {

    if (permissions.size() > 0) {
      listPermissionsNeeded = new ArrayList<String>();

      for (int i = 0; i < permissions.size(); i++) {
        int hasPermission = ContextCompat.checkSelfPermission(current_activity, permissions.get(i));
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
          listPermissionsNeeded.add(permissions.get(i));
        }
      }

      if (!listPermissionsNeeded.isEmpty()) {
        ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), request_code);
        return false;
      }
    }

    return true;
  }

  /**
   * @param requestCode
   * @param permissions
   * @param grantResults
   */
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case 1:
        if (grantResults.length > 0) {
          Map<String, Integer> perms = new HashMap<String, Integer>();

          for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
          }

          final ArrayList<String> pending_permissions = new ArrayList<String>();

          for (int i = 0; i < listPermissionsNeeded.size(); i++) {
            if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
              if (ActivityCompat.shouldShowRequestPermissionRationale(current_activity, listPermissionsNeeded.get(i)))
                pending_permissions.add(listPermissionsNeeded.get(i));
              else {
                Log.i("Go to settings", "and enable permissions");
                //permissionResultCallback.NeverAskAgain(req_code);
                ApplicationUtility.showMessageOKCancel(current_activity, Messages.neverAskAgainMsgTitle, Messages.neverAskAgainMsg,
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                          permissionResultCallback.NeverAskAgain(req_code);
                          break;
                        case DialogInterface.BUTTON_NEGATIVE:
                          Log.i("permisson", "not fully given");
                          break;
                      }
                      ApplicationUtility.isPopupOpen = false;
                    }
                  });
                //Toast.makeText(current_activity, "Go to ", Toast.LENGTH_LONG).show();
                return;
              }
            }
          }

          if (pending_permissions.size() > 0) {
            ApplicationUtility.showMessageOKCancel(current_activity, Messages.title, Messages.message,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                      check_permission(permission_list, dialog_content, req_code);
                      break;
                    case DialogInterface.BUTTON_NEGATIVE:
                      Log.i("permisson", "not fully given");
                      if (permission_list.size() == pending_permissions.size())
                        permissionResultCallback.PermissionDenied(req_code);
                      else
                        permissionResultCallback.PartialPermissionGranted(req_code, pending_permissions);
                      break;
                  }
                  ApplicationUtility.isPopupOpen = false;
                }
              });

          } else {
            Log.i("all", "permissions granted");
            Log.i("proceed", "to next step");
            permissionResultCallback.PermissionGranted(req_code);
          }
        }
        break;
    }
  }

}
