package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PrevWeather;

public class NotificationUtil {

  public static String generateWateringTip(Weather currentWeather, PrevWeather prevWeather) {
    String currDescription = (currentWeather.getForecastDescriptions().get(0)).toLowerCase();
    String prev1Description = (prevWeather.getForecastDescriptions().get(0)).toLowerCase();
    String prev2Description = (prevWeather.getForecastDescriptions().get(1)).toLowerCase();
    if (currDescription.contains("rain")) {
      return "Outdoor plants don’t need any water today";
    } else if (prev1Description.contains("sunny") && prev2Description.contains("sunny")) {
      return "There hasn’t been any rain recently, make sure to water your plants if they need it";
    }
    return null;
  }
}
