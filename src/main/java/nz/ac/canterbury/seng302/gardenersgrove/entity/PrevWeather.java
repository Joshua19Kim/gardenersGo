package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing the previous weather conditions
 * Is a separate entity from the Weather entity because of differing forecasts
 */
public class PrevWeather {
    @JsonProperty("location")
    private JsonNode location;

    @JsonProperty("forecast")
    private JsonNode forecast;
    private List<Float> forecastTemperatures = new ArrayList<Float>();
    private List<String> forecastImages = new ArrayList<String>();
    private List<String> forecastDescriptions = new ArrayList<>();
    private List<Integer> forecastHumidities = new ArrayList<>();
    private List<String> forecastDates = new ArrayList<>();


    public PrevWeather() {
        // Default constructor
    }

    public JsonNode getLocation() {
        return location;
    }

    public void setLocation(JsonNode location) {
        this.location = location;
    }


    /**
     * Sets the forecast information and processes the data to populate the forecast details.
     *
     * @param forecast The forecast information as a JsonNode.
     */
    public void setForecast(JsonNode forecast) {
        this.forecast = forecast;
        for (int i = 0; i < 2; i++) {
            this.forecastDates.add(LocalDate.parse(forecast.get("forecastday").get(i).get("date").asText()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            this.forecastTemperatures.add(forecast.get("forecastday").get(i).get("day").get("avgtemp_c").floatValue());
            this.forecastImages.add(forecast.get("forecastday").get(i).get("day").get("condition").get("icon").asText().replace("64x64", "128x128"));
            this.forecastDescriptions.add(forecast.get("forecastday").get(i).get("day").get("condition").get("text").asText());
            this.forecastHumidities.add(forecast.get("forecastday").get(i).get("day").get("avghumidity").intValue());
        }
    }


    public List<Float> getForecastTemperatures() {
        return forecastTemperatures;
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


    public List<String> getForecastDates() {
        return forecastDates;
    }
}
