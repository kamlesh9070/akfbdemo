package org.dadabhagwan.AKonnect.utils;

import com.google.gson.Gson;

import org.dadabhagwan.AKonnect.dto.ServerResponseDTO;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class WSUtils {

  public static <T> ServerResponseDTO<T> getResponse(String response, final Class<T> dataClass) {
    return new Gson().fromJson(response, getType(ServerResponseDTO.class, dataClass));
  }

  private static Type getType(final Class<?> rawClass, final Class<?> parameterClass) {
    return new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
        return new Type[]{parameterClass};
      }
      @Override
      public Type getRawType() {
        return rawClass;
      }
      @Override
      public Type getOwnerType() {
        return null;
      }
    };
  }
}
