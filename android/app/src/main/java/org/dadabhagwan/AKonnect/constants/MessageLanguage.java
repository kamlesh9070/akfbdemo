package org.dadabhagwan.AKonnect.constants;

import java.util.Arrays;

public enum MessageLanguage {


  ENGLISH("English"), GUJARATI("Gujarati"), HINDI("Hindi");
  String name;

  MessageLanguage(String name) {
    this.name = name;
  }


  public static MessageLanguage fromString(String lang) {
    for (MessageLanguage mL : MessageLanguage.values()) {
      if (mL.name.equalsIgnoreCase(lang)) {
        return mL;
      }
    }
    return null;
  }

}
