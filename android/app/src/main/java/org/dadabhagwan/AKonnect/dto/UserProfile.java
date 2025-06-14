package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfile {

  @SerializedName("subscriber")
  String subscriber;

  @SerializedName("app_lang")
  String appLanguage = "English";
  @SerializedName("pref_msg_lang")
  String prefMsgLang = "English";

  @SerializedName("sender")
  List<Sender> senderChannelList;

  public String getAppLanguage() {
    return appLanguage;
  }

  public void setAppLanguage(String appLanguage) {
    this.appLanguage = appLanguage;
  }

  public String getPrefMsgLang() {
    return prefMsgLang;
  }

  public void setPrefMsgLang(String prefMsgLang) {
    this.prefMsgLang = prefMsgLang;
  }

  public String getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(String subscriber) {
    this.subscriber = subscriber;
  }

  public List<Sender> getSenderChannelList() {
    return senderChannelList;
  }

  public void setSenderChannelList(List<Sender> senderChannelList) {
    this.senderChannelList = senderChannelList;
  }

  @Override
  public String toString() {
    return "UserProfile{" +
      "subscriber='" + subscriber + '\'' +
      ", appLanguage='" + appLanguage + '\'' +
      ", prefMsgLang='" + prefMsgLang + '\'' +
      ", senderChannelList=" + senderChannelList +
      '}';
  }
}
