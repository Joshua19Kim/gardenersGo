package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrevWeather {
    Logger logger = LoggerFactory.getLogger(Weather.class);
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

    }

    public JsonNode getLocation() {
        return location;
    }

    public void setLocation(JsonNode location) {
        this.location = location;
    }


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
