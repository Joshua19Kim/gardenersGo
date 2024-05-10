package nz.ac.canterbury.seng302.gardenersgrove.service;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

@Service
public class WeatherService {
    private static final String API_KEY = "bab0c1cda9964bf489834119241005";
    private static final String CURRENT_WEATHER_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String FORECAST_WEATHER_URL = "http://api.weatherapi.com/v1/forecast.json";


    public static String getCurrentWeather(String location) throws IOException, URISyntaxException {
        String uri = CURRENT_WEATHER_URL + "?key=" + API_KEY + "&q=" + location + "&aqi=no";
        URL url = new URI(uri).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    public static String getForecast(String location) throws IOException, URISyntaxException {
        String uri = FORECAST_WEATHER_URL + "?key=" + API_KEY + "&q=" + location + "&days=1" + "&aqi=no";
        URL url = new URI(uri).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }
}
