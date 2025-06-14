package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class ServerResponseDTO<T> {

  @SerializedName("data")
  T data;

  @SerializedName("success")
  boolean success;

  @SerializedName("message")
  String message;

  @SerializedName("code")
  int code;

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return "ServerResponseDTO{" +
      "data=" + data +
      ", success=" + success +
      ", message='" + message + '\'' +
      ", code=" + code +
      '}';
  }
}
