package org.dadabhagwan.AKonnect;

import android.util.Log;

import com.google.gson.Gson;

import org.dadabhagwan.AKonnect.dto.NotificationPullRes;

public class MyTestClass {
  public static final String TAG = "MyTestClass";

  public static void testMsg() {
    Log.v(TAG, "Inside testMsg");
  }

  public static void main(String[] arg) {

    System.out.println("Test Program");
    String json = "{\"message_list\":[{\"message_id\":24335,\"creation\":\"2020-10-08 22:11:49\",\"subscriber\":\"494866\",\"channel_id\":\"459242\",\"channel_name\":\"Amba Tech Updates\",\"guj_title\":\"\",\"guj_content\":\"\",\"hindi_title\":\"\",\"hindi_content\":\"\",\"eng_title\":\"eng\",\"eng_content\":\"\",\"pinned_until_datetime\":null,\"is_recalled\":0,\"recalled_subscriber\":null,\"recalled_message_id\":0,\"message_attachments\":[]}],\"profile_hash\":\"4da7ae64c6ce5f5571e5522bb471687b7fe8ad70\",\"token\":null,\"is_live\":null}";

    new Gson().fromJson(json, NotificationPullRes.class);
  }
}
