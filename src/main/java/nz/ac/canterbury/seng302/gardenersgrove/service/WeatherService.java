package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * Connects to WeatherAPI and downloads the data for the current weather from a JSON into a Weather object
     * by making use of the jackson object mapper. If there is no weather data for the location it will return null.
     *
     * @param location the location to get the current weather of
     * @return a object representing the details of the current weather
     * @throws IOException        if there is an issue with the connection
     * @throws URISyntaxException if there is an issue with the URI
     */
    @Cacheable(value = "currentWeather", key = "#location")
    public Weather getWeather(String location) throws IOException, URISyntaxException {
        location = location.replace(" ", "-");
        String uri = FORECAST_WEATHER_URL + "?key=" + api_key + "&q=" + location + "&aqi=no" + "&days=3";
        URL url = new URI(uri).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Weather weather =
                    objectMapper.readValue(url, Weather.class);
            logger.info("WEATHER FROM JACKSON: " + weather.getCurrentLocation());
            return weather;
        } catch (IOException ex) {
            // this occurs when no weather is found for that location.
            return null;
        }

    }
    // currently trying to get the last wo days by altering the first date in the uri, notworking
    // Cannot deserialize value of type `[Lnz.ac.canterbury.seng302.gardenersgrove.entity.Weather;` from Object value (token `JsonToken.START_OBJECT`
    public List<Weather> getPrevWeather(String location) throws IOException, URISyntaxException {
        logger.info("Trying to get previous weather");

        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        LocalDate oneDayAgo = LocalDate.now().minusDays(1);

        location = location.replace(" ", "-");

        List<Weather> weatherList = new ArrayList<>();

        try {
            String uriTwoDaysAgo = HISTORY_WEATHER_URL + "?key=" + api_key + "&q=" + location + "&aqi=no" +
                    "&dt=" + twoDaysAgo;
            logger.info("URI for two days ago: " + uriTwoDaysAgo);
            String uriOneDayAgo = HISTORY_WEATHER_URL + "?key=" + api_key + "&q=" + location + "&aqi=no" +
                    "&dt=" + oneDayAgo;
            logger.info("URI for one day ago: " + uriOneDayAgo);

            URL urlTwoDaysAgo = new URI(uriTwoDaysAgo).toURL();
            HttpURLConnection connectionTwoDaysAgo = (HttpURLConnection) urlTwoDaysAgo.openConnection();
            connectionTwoDaysAgo.setRequestMethod("GET");

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Weather[] weathersTwoDaysAgo = objectMapper.readValue(urlTwoDaysAgo, Weather[].class);

            if (weathersTwoDaysAgo != null && weathersTwoDaysAgo.length > 0) {
                weatherList.addAll(Arrays.asList(weathersTwoDaysAgo));
                logger.info("Weather fetched for two days ago: " + weathersTwoDaysAgo[0].getDate());
            }


            URL urlOneDayAgo = new URI(uriOneDayAgo).toURL();
            HttpURLConnection connectionOneDayAgo = (HttpURLConnection) urlOneDayAgo.openConnection();
            connectionOneDayAgo.setRequestMethod("GET");

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Weather[] weathersOneDayAgo = objectMapper.readValue(urlOneDayAgo, Weather[].class);

            if (weathersOneDayAgo != null && weathersOneDayAgo.length > 0) {
                weatherList.addAll(Arrays.asList(weathersOneDayAgo));
                logger.info("Weather fetched for one day ago: " + weathersOneDayAgo[0].getDate());
            }

            logger.info("Weather retrieved successfully");

        } catch (IOException ex) {
            // this occurs when no weather is found for that location.
            return null;
        } catch (URISyntaxException ex) {
            // this occurs when no weather is found for that location.
            return null;
        }

        return weatherList;
    }


    /**
     * Used to clear the cache every hour to ensure that the weather data is not stale
     */
    @CacheEvict(value = "currentWeather", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.currentWeatherTTL}")
    public void emptyCurrentWeatherCache() {
        logger.info("Emptying current weather cache");
    }
}

