package org.dadabhagwan.AKonnect.dto;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.dadabhagwan.AKonnect.ApplicationUtility;
import org.dadabhagwan.AKonnect.SharedPreferencesTask;
import org.dadabhagwan.AKonnect.constants.MessageLanguage;

public class NotificationDTO {

  protected static final String TAG = "NotificationDTO";
  String notificationTitle;

  @SerializedName("guj_title")
  String gujTitle;
  @SerializedName("hindi_title")
  String hindiTitle;
  @SerializedName("eng_title")
  String engTitle;
  @SerializedName("message_id")
  int messageId;
  @SerializedName("channel_name")
  String channelName;
  @SerializedName("sender_subscriber")
  String senderSubscriber;
  @SerializedName("channel_id")
  String channelId;

  @SerializedName("avatar_url")
  String avatarUrl;

  @SerializedName("recalled_message_id")
  int recalledMessageId;

  @SerializedName("live_notification")
  String liveNotification;

  PullNotificationDTO pullNotificationDTO;

  @Override
  public String toString() {
    return "NotificationDTO{" +
      "notificationTitle='" + notificationTitle + '\'' +
      ", gujTitle='" + gujTitle + '\'' +
      ", hindiTitle='" + hindiTitle + '\'' +
      ", engTitle='" + engTitle + '\'' +
      ", messageId=" + messageId +
      ", channelName='" + channelName + '\'' +
      ", senderSubscriber='" + senderSubscriber + '\'' +
      ", channelId='" + channelId + '\'' +
      ", avatarUrl='" + avatarUrl + '\'' +
      ", recalledMessageId=" + recalledMessageId +
      ", liveNotification='" + liveNotification + '\'' +
      '}';
  }

  public PullNotificationDTO getPullNotificationDTO() {
    return pullNotificationDTO;
  }

  public void setPullNotificationDTO(PullNotificationDTO pullNotificationDTO) {
    this.pullNotificationDTO = pullNotificationDTO;
  }

  public boolean isReacall() {
    return recalledMessageId > 0;
  }

  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getNotificationTitle(Context context) {
    if(ApplicationUtility.isStrNullOrEmpty(notificationTitle))
      setTitle(context);
    return notificationTitle;
  }

  public void setNotificationTitle(String notificationTitle) {
    this.notificationTitle = notificationTitle;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getGujTitle() {
    return gujTitle;
  }

  public void setGujTitle(String gujTitle) {
    this.gujTitle = gujTitle;
  }

  public String getHindiTitle() {
    return hindiTitle;
  }

  public void setHindiTitle(String hindiTitle) {
    this.hindiTitle = hindiTitle;
  }

  public String getEngTitle() {
    return engTitle;
  }

  public void setEngTitle(String engTitle) {
    this.engTitle = engTitle;
  }

  public String getSenderSubscriber() {
    return senderSubscriber;
  }

  public void setSenderSubscriber(String senderSubscriber) {
    this.senderSubscriber = senderSubscriber;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public int getRecalledMessageId() {
    return recalledMessageId;
  }

  public void setRecalledMessageId(int recalledMessageId) {
    this.recalledMessageId = recalledMessageId;
  }

  public boolean isLiveNotification() {
    return !ApplicationUtility.isStrNullOrEmpty(liveNotification) && liveNotification.equalsIgnoreCase("true");
  }

  public void setLiveNotification(String liveNotification) {
    this.liveNotification = liveNotification;
  }

  private void setTitle(Context context) {
    String title = null;
    UserProfile userProfile = SharedPreferencesTask.getUserProfile(context);
    Log.d(TAG, "$$$$$$ msgLag:" + userProfile);
    if (userProfile != null) {
      MessageLanguage mLang = MessageLanguage.fromString(userProfile.getPrefMsgLang());
      Log.d(TAG, "$$$$$$ mLang:" + mLang);
      if (mLang != null) {
        switch (mLang) {
          case ENGLISH:
            title = getEngTitle();
            break;
          case GUJARATI:
            title = getGujTitle();
            break;
          case HINDI:
            title = getHindiTitle();
            break;
        }
      }
    }
    if (ApplicationUtility.isStrNullOrEmpty(title)) {
      title = getEngTitle();
      if (ApplicationUtility.isStrNullOrEmpty(title))
        title = getGujTitle();
      if (ApplicationUtility.isStrNullOrEmpty(title))
        title = getHindiTitle();
    }
    Log.d(TAG, "$$$$$$ title:" + title);
    setNotificationTitle(title);
  }
}
