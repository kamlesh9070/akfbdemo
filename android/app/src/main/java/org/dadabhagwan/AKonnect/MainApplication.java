package org.dadabhagwan.AKonnect;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MainApplication extends Application {
  private static final String TAG = "AKonnect.MainApp";

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "MainApplication.onCreate");
  }
}
