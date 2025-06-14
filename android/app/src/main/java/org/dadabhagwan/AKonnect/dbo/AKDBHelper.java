package org.dadabhagwan.AKonnect.dbo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.dadabhagwan.AKonnect.dbo.model.NotificationLogTO;


public class AKDBHelper extends SQLiteOpenHelper {

  //Test commit
  protected static final String TAG = "AKonnect[AKDBHelper]";

  // Database Version
  private static final int DATABASE_VERSION = 2;

  // Database Name
  private static final String DATABASE_NAME = "AkonnectDb.db";

  private static AKDBHelper mInstance = null;

  public static synchronized AKDBHelper getInstance(Context ctx) {

    // Use the application context, which will ensure that you
    // don't accidentally leak an Activity's context.
    // See this article for more information: http://bit.ly/6LRzfx

    if (mInstance == null) {
      mInstance = new AKDBHelper(ctx.getApplicationContext());
    }
    return mInstance;
  }

  /**
   * Constructor should be private to prevent direct instantiation.
   * make call to static factory method "getInstance()" instead.
   */
  private AKDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }


  public int getMaxMsgIdFromMessageMaster() {
    int maxMsgId = 0;
    Cursor cursor = null;
    try {
      String maxMsgIdQuery = "SELECT MAX(message_id) AS last_message_id FROM MessageMaster";
      SQLiteDatabase db = this.getReadableDatabase();
      cursor = db.rawQuery(maxMsgIdQuery, null);

      if (cursor != null && cursor.getCount() > 0) {
        cursor.moveToFirst();
        maxMsgId = cursor.getInt(0);
      }
      cursor.close();
    } catch (Exception e) {
      Log.e(TAG, " Exception getMaxMsgIdFromMessageMaster : " + e.getMessage());
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }

    Log.d(TAG, " getMaxMsgIdFromMessageMaster : maxMsgId --> " + maxMsgId);
    // return maxMsgId
    return maxMsgId;
  }


}
