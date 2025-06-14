package org.dadabhagwan.AKonnect;

public class ImageAndTitleMapper {

  private static final String AKONNECT_IMAGE = "akonnect";
  private static final String DIMPLEBHAI = "dimplebhai";
  private static final String TODAYS_ENERGIZER = "todays_energizer";
  private static final String ADALAJ_UPDATES = "adalaj_updates";
  private static final String SHEEL_SADHAK = "sheel_sadhak";
  private static final String DEUTSCHE_NEWS = "deutsche_news";
  private static final String DELHI_UPDATES = "delhi_updates";
//  private static final String DEFAULT = "amlogo";
  private static final String DEFAULT = "ic_launcher";

  public static String getImageName(String title) {
    if (title == null) {
      return DEFAULT;
    } else {
      title = title.trim();
      if (title.equalsIgnoreCase("Today's Energizer"))
        return TODAYS_ENERGIZER;
      else if (title.equalsIgnoreCase("Akonnect"))
        return AKONNECT_IMAGE;
      else if (title.equalsIgnoreCase("Dimplebhai"))
        return DIMPLEBHAI;
      else if (title.equalsIgnoreCase("Adalaj Updates"))
        return ADALAJ_UPDATES;
      else if (title.equalsIgnoreCase("Sheel Sadhak"))
        return SHEEL_SADHAK;
      else if (title.equalsIgnoreCase("Delhi Updates"))
        return DELHI_UPDATES;
      else if (title.equalsIgnoreCase("Deutsche News"))
        return DEUTSCHE_NEWS;
      else
        return DEFAULT;
    }
  }


}
