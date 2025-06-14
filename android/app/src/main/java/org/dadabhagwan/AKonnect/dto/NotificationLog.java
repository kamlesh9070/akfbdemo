package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

import org.dadabhagwan.AKonnect.constants.WSConstant;

import java.util.Date;

public class NotificationLog {

  @SerializedName("device_id")
  String deviceID;
  @SerializedName("device_model")
  String deviceModel;
  @SerializedName("device_os")
  String deviceOS;
  @SerializedName("os_version")
  String osVersion;
  @SerializedName("app_version")
  String appVersion;
  @SerializedName("device")
  String device;
  @SerializedName("client_id")
  String clientID;
  @SerializedName("timestamp")
  long timestamp;

  @SerializedName("network")
  String network;
  @SerializedName("subscriber")
  String subscriber;


  @SerializedName("message_id")
  int messageId;
  @SerializedName("title")
  String title;
  @SerializedName("channel_id")
  String channelId;
  @SerializedName("channel_name")
  String channelName;
  @SerializedName("status")
  String status;
  @SerializedName("push_type")
  String pushType;

  @SerializedName("last_message")
  int lastMessageId;

  @SerializedName("is_coordinator")
  int isCoordinator;

  public void setFieldsFromDTO(DeviceDetail deviceDetail) {
    if(deviceDetail != null) {
      deviceID = deviceDetail.getDeviceId();
      deviceModel = deviceDetail.getDeviceModel();
      deviceOS = deviceDetail.getDeviceOs();
      osVersion = deviceDetail.getOsVersion();
      appVersion = deviceDetail.getAppVersion();
      clientID = deviceDetail.getClientId();
      timestamp = new Date().getTime();
    }
  }

  public void setFieldsFromUserProfile(UserProfile userProfile) {
    if(userProfile != null) {
      subscriber = userProfile.getSubscriber();
    }
  }

  public void setFieldsFromNDTO(String pushType, NotificationDTO ndto) {
    if(ndto != null) {
      messageId = ndto.getMessageId();
      title = ndto.getEngTitle();
      channelId = ndto.getChannelId();
      channelName = ndto.getChannelName();
      status = WSConstant.STATUS_NOTIFIED;
      this.pushType = pushType;
      if(ndto.getPullNotificationDTO() != null) {
        PullNotificationDTO pDTO = ndto.getPullNotificationDTO();
        this.lastMessageId = pDTO.getLastMessageId();
        this.isCoordinator = pDTO.getIsCoordinator();
      }
    }
  }

  public void setFieldsFromDTO(UserRegData userRegData) {
    if(userRegData != null) {
      device = userRegData.getDevice();
    }
  }
  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getClientID() {
    return clientID;
  }

  public void setClientID(String clientID) {
    this.clientID = clientID;
  }

  public String getDeviceID() {
    return deviceID;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel) {
    this.deviceModel = deviceModel;
  }

  public String getDeviceOS() {
    return deviceOS;
  }

  public void setDeviceOS(String deviceOS) {
    this.deviceOS = deviceOS;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public int getLastMessageId() {
    return lastMessageId;
  }

  public void setLastMessageId(int lastMessageId) {
    this.lastMessageId = lastMessageId;
  }

  public int getIsCoordinator() {
    return isCoordinator;
  }

  public void setIsCoordinator(int isCoordinator) {
    this.isCoordinator = isCoordinator;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public String getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(String subscriber) {
    this.subscriber = subscriber;
  }

  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPushType() {
    return pushType;
  }

  public void setPushType(String pushType) {
    this.pushType = pushType;
  }

  @Override
  public String toString() {
    return "NotificationLog{" +
      "deviceID='" + deviceID + '\'' +
      ", deviceModel='" + deviceModel + '\'' +
      ", deviceOS='" + deviceOS + '\'' +
      ", osVersion='" + osVersion + '\'' +
      ", appVersion='" + appVersion + '\'' +
      ", device='" + device + '\'' +
      ", clientID='" + clientID + '\'' +
      ", timestamp=" + timestamp +
      ", network='" + network + '\'' +
      ", subscriber='" + subscriber + '\'' +
      ", messageId=" + messageId +
      ", title='" + title + '\'' +
      ", channelId='" + channelId + '\'' +
      ", channelName='" + channelName + '\'' +
      ", status='" + status + '\'' +
      ", pushType='" + pushType + '\'' +
      '}';
  }
}
