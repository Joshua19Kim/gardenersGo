package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PrevWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

public class WeatherServiceTest {

    private ObjectMapper objectMapper;

    private WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        objectMapper = Mockito.mock(ObjectMapper.class);
        weatherService = new WeatherService("", objectMapper);
    }

    @Test
    public void getWeather_ValidLocation_WeatherReturned() throws IOException, URISyntaxException {
        String location = "Auckland";
        Weather expectedWeather = new Weather();
        when(objectMapper.readValue(any(URL.class), eq(Weather.class))).thenReturn(expectedWeather);
        Weather actualWeather = weatherService.getWeather(location);

        Assertions.assertEquals(expectedWeather, actualWeather);

    }

    @Test
    public void getWeather_InvalidLocation_NullReturned() throws IOException, URISyntaxException {
        String location = "12345";

        when(objectMapper.readValue(any(URL.class), eq(Weather.class))).thenThrow(IOException.class);

        Weather actualWeather = weatherService.getWeather(location);

        Assertions.assertNull(actualWeather);
    }

    @Test
    public void getPreviousWeather_ValidLocation_WeatherReturned() throws IOException, URISyntaxException {
        String location = "Auckland";
        PrevWeather expectedWeather = new PrevWeather();
        when(objectMapper.readValue(any(URL.class), eq(PrevWeather.class))).thenReturn(expectedWeather);
        PrevWeather actualWeather = weatherService.getPrevWeather(location);

        Assertions.assertEquals(expectedWeather, actualWeather);

    }

    @Test
    public void getPreviousWeather_InvalidLocation_NullReturned() throws IOException, URISyntaxException {
        String location = "12345";

        when(objectMapper.readValue(any(URL.class), eq(PrevWeather.class))).thenThrow(IOException.class);

        PrevWeather actualWeather = weatherService.getPrevWeather(location);

        Assertions.assertNull(actualWeather);
    }

}
