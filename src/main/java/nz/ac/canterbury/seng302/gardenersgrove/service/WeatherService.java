package nz.ac.canterbury.seng302.gardenersgrove.service;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.util.HashMap;

@Service
public class WeatherService {
    Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static String api_key;
    private static final String CURRENT_WEATHER_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String FORECAST_WEATHER_URL = "http://api.weatherapi.com/v1/forecast.json";

    @Autowired
    public WeatherService(@Value("${weather.password}") String api_key) {
        WeatherService.api_key = api_key;
    }

    public HashMap<String, LinkedTreeMap<String, Object>> getCurrentWeather(String location) throws IOException, URISyntaxException {
        String uri = CURRENT_WEATHER_URL + "?key=" + api_key + "&q=" + location + "&aqi=no";
        URL url = new URI(uri).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, LinkedTreeMap<String, Object>>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException ioException) {
            logger.error(ioException.toString());
            return null;
        } finally {
            connection.disconnect();
        }
    }


    public HashMap<String, LinkedTreeMap<String, Object>> getForecast(String location) throws IOException, URISyntaxException {
        String uri = FORECAST_WEATHER_URL + "?key=" + api_key + "&q=" + location + "&days=1" + "&aqi=no";
        URL url = new URI(uri).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, LinkedTreeMap<String, Object>>>() {}.getType();
            return gson.fromJson(reader, type);
        } finally {
            connection.disconnect();
        }
    }
}

