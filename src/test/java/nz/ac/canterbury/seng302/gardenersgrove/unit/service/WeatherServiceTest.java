package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WeatherServiceTest {

    private ObjectMapper objectMapper;

    private WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        objectMapper = Mockito.mock(ObjectMapper.class);
        weatherService = new WeatherService("", objectMapper);
    }

    @Test
    public void GetCurrentWeather_ValidLocation_WeatherReturned() throws IOException, URISyntaxException {
        String location = "Auckland";
        Weather expectedWeather = new Weather();
        when(objectMapper.readValue(any(URL.class), eq(Weather.class))).thenReturn(expectedWeather);
        Weather actualWeather = weatherService.getCurrentWeather(location);

        Assertions.assertEquals(expectedWeather, actualWeather);

    }

    @Test
    public void GetCurrentWeather_InvalidLocation_NullReturned() throws IOException, URISyntaxException {
        String location = "12345";

        when(objectMapper.readValue(any(URL.class), eq(Weather.class))).thenThrow(IOException.class);

        Weather actualWeather = weatherService.getCurrentWeather(location);

        Assertions.assertNull(actualWeather);
    }
}
