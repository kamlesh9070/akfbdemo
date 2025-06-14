package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class ChannelDetails {

  @SerializedName("channel_id")
  String channelId;
  @SerializedName("channel_name")
  String channelName;
  @SerializedName("avatar_url")
  String avatarUrl;

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

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  @Override
  public String toString() {
    return "ChannelDetails{" +
      "channelId='" + channelId + '\'' +
      ", channelName='" + channelName + '\'' +
      ", avatarUrl='" + avatarUrl + '\'' +
      '}';
  }
}
