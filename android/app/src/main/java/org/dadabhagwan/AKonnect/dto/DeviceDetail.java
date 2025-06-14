package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class DeviceDetail {
  @SerializedName("app_version")
  String appVersion;
  @SerializedName("client_id")
  String clientId;
  @SerializedName("device_id")
  String deviceId;
  @SerializedName("device_model")
  String deviceModel;
  @SerializedName("device_os")
  String deviceOs;
  String networkType;
  @SerializedName("os_version")
  String osVersion;
  @SerializedName("device_token")
  String deviceToken;
  int orientation;
  int androidSdkVersion;

  @Override
  public String toString() {
    return "DeviceDetail{" +
      "appVersion='" + appVersion + '\'' +
      ", clientId='" + clientId + '\'' +
      ", deviceId='" + deviceId + '\'' +
      ", deviceModel='" + deviceModel + '\'' +
      ", deviceOs='" + deviceOs + '\'' +
      ", networkType='" + networkType + '\'' +
      ", osVersion='" + osVersion + '\'' +
      ", deviceToken='" + deviceToken + '\'' +
      ", orientation=" + orientation +
      ", androidSdkVersion=" + androidSdkVersion +
      '}';
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel) {
    this.deviceModel = deviceModel;
  }

  public String getDeviceOs() {
    return deviceOs;
  }

  public void setDeviceOs(String deviceOs) {
    this.deviceOs = deviceOs;
  }

  public String getNetworkType() {
    return networkType;
  }

  public void setNetworkType(String networkType) {
    this.networkType = networkType;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  public int getOrientation() {
    return orientation;
  }

  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }

  public int getAndroidSdkVersion() {
    return androidSdkVersion;
  }

  public void setAndroidSdkVersion(int androidSdkVersion) {
    this.androidSdkVersion = androidSdkVersion;
  }
}
