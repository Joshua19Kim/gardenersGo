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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

@Service
public class WeatherService {
    Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static String api_key;
    private static final String CURRENT_WEATHER_URL = "https://api.weatherapi.com/v1/current.json";
    private static final String FORECAST_WEATHER_URL = "https://api.weatherapi.com/v1/forecast.json";
    private final ObjectMapper objectMapper;

    @Autowired
    public WeatherService(@Value("${weather.password}") String api_key, ObjectMapper objectMapper) {
        WeatherService.api_key = api_key;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "currentWeather", key="#location")
    public Weather getCurrentWeather(String location) throws IOException, URISyntaxException {
        location = location.replace(" ", "-");
        String uri = CURRENT_WEATHER_URL + "?key=" + api_key + "&q=" + location + "&aqi=no";
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

    @CacheEvict(value = "currentWeather", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.currentWeatherTTL}")
    public void emptyCurrentWeatherCache() {
        logger.info("Emptying current weather cache");
    }
}

