package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PrevWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
  Logger logger = LoggerFactory.getLogger(WeatherService.class);
  private static String api_key;
  private static final String FORECAST_WEATHER_URL = "https://api.weatherapi.com/v1/forecast.json";
  private static final String HISTORY_WEATHER_URL = "https://api.weatherapi.com/v1/history.json";
  private final ObjectMapper objectMapper;

  @Autowired
  public WeatherService(@Value("${weather.password}") String api_key, ObjectMapper objectMapper) {
    WeatherService.api_key = api_key;
    this.objectMapper = objectMapper;
  }

  /**
   * Connects to WeatherAPI and downloads the data for the current weather from a JSON into a
   * Weather object by making use of the jackson object mapper. If there is no weather data for the
   * location it will return null.
   *
   * @param location the location to get the current weather of
   * @return a object representing the details of the current weather
   * @throws IOException if there is an issue with the connection
   * @throws URISyntaxException if there is an issue with the URI
   */
  @Cacheable(value = "currentWeather", key = "#location")
  public Weather getWeather(String location) throws IOException, URISyntaxException {
    location =location.trim().replace(" ", "-");

    String uri = FORECAST_WEATHER_URL + "?key=" + api_key.trim() + "&q=" + location + "&aqi=no&days=3";
    URL url = new URI(uri).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      Weather weather = objectMapper.readValue(url, Weather.class);
      logger.info("WEATHER FROM JACKSON: " + weather.getCurrentLocation());
      return weather;
    } catch (IOException ex) {
      // this occurs when no weather is found for that location.
      return null;
    }
  }

  @Cacheable(value = "prevWeather", key = "#location")
  public PrevWeather getPrevWeather(String location) throws IOException, URISyntaxException {

    LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
    LocalDate oneDayAgo = LocalDate.now().minusDays(1);

    location = location.replace(" ", "-");

    String uri =
        HISTORY_WEATHER_URL
            + "?key="
            + api_key.trim()
            + "&q="
            + location
            + "&aqi=no"
            + "&dt="
            + twoDaysAgo
            + "&end_dt="
            + oneDayAgo;

    URL url = new URI(uri).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
      PrevWeather weather = objectMapper.readValue(url, PrevWeather.class);

      return weather;
    } catch (IOException ex) {
      // This occurs when no weather is found for that location.
      return null;
    }
  }

  /** Used to clear the cache every hour to ensure that the weather data is not stale */
  @CacheEvict(value = {"currentWeather", "prevWeather"}, allEntries = true)
  @Scheduled(fixedRateString = "${caching.spring.currentWeatherTTL}")
  public void emptyWeatherCache() {
    logger.info("Emptying weather cache");
  }


}
