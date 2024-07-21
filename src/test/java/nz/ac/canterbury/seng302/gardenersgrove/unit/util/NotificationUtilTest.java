package nz.ac.canterbury.seng302.gardenersgrove.unit.util;

import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantControllers.PlantAddFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PrevWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.util.NotificationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationUtilTest {

    private WeatherService weatherService;
    @BeforeEach
    public void setUp() {
        weatherService = Mockito.mock(WeatherService.class);
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "Rain: Outdoor plants don’t need any water today",
                    "Light Rain: Outdoor plants don’t need any water today",
                    "Sunny: There hasn’t been any rain recently, make sure to water your plants if they need it",
                    "Clear: "
            },
            delimiter = ':')
    public void GetWeather_BasedOnDescription_NotificationReturned(String description, String expectedMessage) throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"Sunny", "Sunny", "Clear"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn(description);
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        PrevWeather prevWeather = Mockito.mock(PrevWeather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(prevWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(prevWeather.getForecastTemperatures()).thenReturn(List.of(forecastTemperatures));
        when(prevWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(prevWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(prevWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        String result = NotificationUtil.generateWateringTip(currentWeather, prevWeather);
        if (expectedMessage == null) {
            assertFalse(result.isEmpty());
        } else {
            assertFalse(result.isEmpty());
            assertEquals(expectedMessage, result);
        }
    }
}
