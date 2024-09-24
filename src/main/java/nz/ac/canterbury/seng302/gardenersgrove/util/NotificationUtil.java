package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.PrevWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;

/**
 * Utility class for generating notifications and tips related to garden care based on weather conditions.
 */
public class NotificationUtil {

    /**
     * Generates a watering tip for garden plants based on the current weather and previous weather data
     *
     * @param currentWeather The current weather data
     * @param prevWeather    The previous weather data
     * @return A string containing the watering tip. If no specific tip, returns null
     */
    public static String generateWateringTip(Weather currentWeather, PrevWeather prevWeather) {
        String currDescription = (currentWeather.getWeatherDescription()).toLowerCase();
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
