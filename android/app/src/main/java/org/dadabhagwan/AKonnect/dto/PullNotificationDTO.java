package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class PullNotificationDTO {

  @SerializedName("last_message")
  int lastMessageId;

  @SerializedName("token")
  String token;

  @SerializedName("profile_hash")
  String profileHash;

  @SerializedName("is_coordinator")
  int isCoordinator;

  @SerializedName("subscriber")
  String subscriber;

  @SerializedName("device")
  String device;

  @Override
  public String toString() {
    return "PullNotificationDTO{" +
      "lastMessageId=" + lastMessageId +
      ", token='" + token + '\'' +
      ", profileHash='" + profileHash + '\'' +
      ", isCoordinator=" + isCoordinator +
      ", subscriber='" + subscriber + '\'' +
      ", device='" + device + '\'' +
      '}';
  }

  public int getLastMessageId() {
    return lastMessageId;
  }

  public void setLastMessageId(int lastMessageId) {
    this.lastMessageId = lastMessageId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getProfileHash() {
    return profileHash;
  }

  public void setProfileHash(String profileHash) {
    this.profileHash = profileHash;
  }

  public int getIsCoordinator() {
    return isCoordinator;
  }

  public void setIsCoordinator(int isCoordinator) {
    this.isCoordinator = isCoordinator;
  }

  public String getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(String subscriber) {
    this.subscriber = subscriber;
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }
}
