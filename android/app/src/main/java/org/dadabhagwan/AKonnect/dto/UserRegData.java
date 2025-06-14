package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class UserRegData {

  @SerializedName("device")
  String device;

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  @Override
  public String toString() {
    return "UserRegData{" +
      "device='" + device + '\'' +
      '}';
  }
}
