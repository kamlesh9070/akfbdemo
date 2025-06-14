package org.dadabhagwan.AKonnect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtility {

  public static Bitmap getDownloadedImage(String strUrl) {
    URL url = stringToURL(strUrl);
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.connect();
      InputStream inputStream = connection.getInputStream();
      BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
      return BitmapFactory.decodeStream(bufferedInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String bitMapToString(Bitmap bitmap) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
    byte[] b = baos.toByteArray();
    return Base64.encodeToString(b, Base64.DEFAULT);
  }

  public static Bitmap stringToBitMap(String encodedString) {
    try {
      byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
      return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    } catch (Exception e) {
      e.getMessage();
      return null;
    }
  }

  public static URL stringToURL(String strUrl) {
    try {
      return new URL(strUrl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
