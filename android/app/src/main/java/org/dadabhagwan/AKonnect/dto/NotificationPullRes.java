package org.dadabhagwan.AKonnect.dto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class NotificationPullRes {

  @SerializedName("message_list")
  List<NotificationDTO> notificationDTOList;

  @SerializedName("profile_hash")
  String profile_hash;

  @SerializedName("token")
  String token;

  @SerializedName("processFlag")
  boolean processFlag = true;
  public List<NotificationDTO> getNotificationDTOList() {
    return notificationDTOList;
  }

  public void setNotificationDTOList(List<NotificationDTO> notificationDTOList) {
    this.notificationDTOList = notificationDTOList;
  }

  public boolean isProcessFlag() {
    return processFlag;
  }

  public void setProcessFlag(boolean processFlag) {
    this.processFlag = processFlag;
  }

  public String getProfile_hash() {
    return profile_hash;
  }

  public void setProfile_hash(String profile_hash) {
    this.profile_hash = profile_hash;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return "NotificationPullRes{" +
      "notificationDTOList=" + notificationDTOList +
      ", profile_hash='" + profile_hash + '\'' +
      ", token='" + token + '\'' +
      ", processFlag=" + processFlag +
      '}';
  }
}
