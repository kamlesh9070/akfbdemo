package org.dadabhagwan.AKonnect.dbo.model;

public class NotificationLogTO {
  public static final String TABLE_NAME = "NotificationMaster";
  public static final String LIVE_TABLE_NAME = "LiveNotificationMaster";
  public static final String COLUMN_ID = "MsgId";
  public static final String COLUMN_TIMESTAMP = "timestamp";

  private int id;
  private String timestamp;


  // Create table SQL query
  public static final String CREATE_TABLE =
    "CREATE TABLE " + TABLE_NAME + "("
      + COLUMN_ID + " INTEGER PRIMARY KEY,"
      + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
      + ")";

  public static String getCreateTableQuery(String tableName) {
    return "CREATE TABLE " + tableName + "("
      + COLUMN_ID + " INTEGER PRIMARY KEY,"
      + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
      + ")";
  }

  public NotificationLogTO() {
  }

  public NotificationLogTO(int id, String timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  public int getId() {
    return id;
  }


  public String getTimestamp() {
    return timestamp;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
