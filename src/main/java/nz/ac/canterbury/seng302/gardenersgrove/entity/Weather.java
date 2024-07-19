package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class Weather {
    Logger logger = LoggerFactory.getLogger(Weather.class);
    @JsonProperty("location")
    private JsonNode location;

    @JsonProperty(value = "current")
    private JsonNode current;
    @JsonProperty("forecast")
    private JsonNode forecast;
    private List<Float> forecastMinTemperatures = new ArrayList<>();
    private List<Float> forecastMaxTemperatures = new ArrayList<>();
    private List<String> forecastImages = new ArrayList<>();
    private List<String> forecastDescriptions = new ArrayList<>();
    private List<Integer> forecastHumidities = new ArrayList<>();
    private List<String> forecastDates = new ArrayList<>();
    private List<DayOfWeek> forecastDays = new ArrayList<>();
    private String currentLocation;
    private String currentWeatherImage;
    private String currentWeatherDescription;
    private Float currentTemperature;
    private Integer currentHumidity;
    private String date;
    private static final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");


    public Weather() {

    }

    public JsonNode getLocation() {
        return location;
    }

    public void setLocation(JsonNode location) {
        this.location = location;
        this.currentLocation = location.get("name").asText();
    }

    /**
     * Sets the current weather information and processes the data to populate the current weather details.
     *
     * @param current The current weather information as a JsonNode.
     */
    public void setCurrent(JsonNode current) {
        this.current = current;
        this.currentWeatherImage = current.get("condition").get("icon").asText().replace("64x64", "128x128");
        this.currentWeatherDescription = current.get("condition").get("text").asText();
        this.currentTemperature = current.get("temp_c").floatValue();
        this.currentHumidity = current.get("humidity").intValue();
    }

    /**
     * Sets the forecast information and processes the data to populate the forecast details.
     *
     * @param forecast The forecast information as a JsonNode.
     */
    public void setForecast(JsonNode forecast) {
        this.forecast = forecast;
        this.date = LocalDate.parse(forecast.get("forecastday").get(0).get("date").asText()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        for (int i = 0; i < 3; i++) {
            this.forecastDates.add(LocalDate.parse(forecast.get("forecastday").get(i).get("date").asText()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            this.forecastDays.add(LocalDate.parse(forecast.get("forecastday").get(i).get("date").asText()).getDayOfWeek());
            this.forecastMinTemperatures.add(forecast.get("forecastday").get(i).get("day").get("mintemp_c").floatValue());
            this.forecastMaxTemperatures.add(forecast.get("forecastday").get(i).get("day").get("maxtemp_c").floatValue());
            this.forecastImages.add(forecast.get("forecastday").get(i).get("day").get("condition").get("icon").asText().replace("64x64", "128x128"));
            this.forecastDescriptions.add(forecast.get("forecastday").get(i).get("day").get("condition").get("text").asText());
            this.forecastHumidities.add(forecast.get("forecastday").get(i).get("day").get("avghumidity").intValue());
        }
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getWeatherImage() {
        return currentWeatherImage;
    }

    public String getWeatherDescription() {
        return currentWeatherDescription;
    }

    public Float getTemperature() {
        return currentTemperature;
    }

    public Integer getHumidity() {
        return currentHumidity;
    }

    public List<Float> getForecastMinTemperatures() {
        return forecastMinTemperatures;
    }
    public List<Float> getForecastMaxTemperatures() {
        return forecastMaxTemperatures;
    }

    public List<String> getForecastImages() {
        return forecastImages;
    }

    public List<String> getForecastDescriptions() {
        return forecastDescriptions;
    }

    public List<Integer> getForecastHumidities() {
        return forecastHumidities;
    }

    public JsonNode getForecast() {
        return forecast;
    }

    public String getDate() {
        return date;
    }

    /**
     * gets day of week based on date stored in weather object
     *
     * @return DayOfWeek Enum where Monday has a numerical value of 1 and Sunday 7
     */
    public DayOfWeek getDay() {
        return LocalDate.parse(date, inputFormatter).getDayOfWeek();
    }

    public List<String> getForecastDates() {
        return forecastDates;
    }

    public List<DayOfWeek> getForecastDays() {
        return forecastDays;
    }
}
