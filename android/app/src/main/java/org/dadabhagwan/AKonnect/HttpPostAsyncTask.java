package org.dadabhagwan.AKonnect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by coder2 on 07-Mar-2018.
 */


//We kept output as STRING instead of JSONObject, since we are using same Class for Other methods
public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
  protected static final String TAG = "AKonnect[AsyncTask]";
  public AsyncResponseListner delegate = null;

  // This is the JSON body of the post
  String postData;

  String ApiKey = "";
  // This is a constructor that allows you to pass in the JSON body

  public HttpPostAsyncTask(String postData, String apikey, AsyncResponseListner delegate) {
    if (delegate != null) {
      this.postData = postData;
      this.ApiKey = apikey;
      this.delegate = delegate;
    }
  }

  public HttpPostAsyncTask(String postData, String apikey) {
    if (postData != null) {
      this.postData = postData;
      this.ApiKey = apikey;
    }
  }

  // This is a function that we are overriding from AsyncTask.
  // It takes Strings as parameters because that is what we defined for the parameters of our async task
  @Override
  protected String doInBackground(String... params) {
    String response = null;
    try {
      // This is getting the url from the string we passed in
      URL url = new URL(params[0]);
      Log.d(TAG, "Post URL: " + params[0] + "\tdata:" + postData);
      // Create the urlConnection
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setDoInput(true);
      urlConnection.setDoOutput(true);

      urlConnection.setRequestProperty("Content-Type", "application/json");

      urlConnection.setRequestMethod("POST");

      // Send the post body
      if (this.postData != null) {
        //if(delegate == null){
        urlConnection.setRequestProperty("Authorization", ApiKey);
        //}
        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(postData.toString());
        writer.flush();
      }

      int statusCode = urlConnection.getResponseCode();

      if (statusCode == 200 && this.delegate != null) {
        try {
          BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
          }
          br.close();
          response = sb.toString();

          Log.d(TAG, "Success response----------------->" + response);
        } catch (Exception e) {
          Log.d(TAG, "Exception inside if  ------------------------>" + e.getMessage());
          e.printStackTrace();
        }
      } else {
        Log.d(TAG, "Failure statusCode or this.delegate != null ----------------->" + statusCode);
      }
    } catch (Exception e) {
      Log.d(TAG, "Exception ------------------------>" + e.getMessage());
      e.printStackTrace();
    }
    return response;
  }

  @Override
  protected void onPostExecute(String result) {
    Log.d(TAG, "Response: " + result);
    if (delegate != null)
      delegate.onPostExecute(postData, result);
  }


}
