package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Weather {
    @JsonProperty("location")
    private JsonNode location;

    @JsonProperty("current")
    private JsonNode current;
    @JsonProperty("forecast")
    private JsonNode forecast;
    private List<Float> forecastTemperatures = new ArrayList<Float>();
    private List<String> forecastImages = new ArrayList<String>();
    private List<String> forecastDescriptions = new ArrayList<String>();
    private List<Float> forecastHumidities = new ArrayList<Float>();
    private String currentLocation;
    private String currentWeatherImage;
    private String currentWeatherDescription;
    private Float currentTemperature;
    private Float currentHumidity;

    public Weather() {

    }

    public JsonNode getLocation() {
        return location;
    }

    public void setLocation(JsonNode location) {
        this.location = location;
        this.currentLocation = location.get("name").asText();
    }

    public void setCurrent(JsonNode current) {
        this.current = current;
        this.currentWeatherImage = current.get("condition").get("icon").asText().replace("64x64", "128x128");
        this.currentWeatherDescription = current.get("condition").get("text").asText();
        this.currentTemperature = current.get("temp_c").floatValue();
        this.currentHumidity = current.get("humidity").floatValue();
    }

    public void setForecast(JsonNode forecast) {
        this.forecast = forecast;
        this.forecastTemperatures.add(forecast.get("forecastday").get(0).get("day").get("avgtemp_c").floatValue());
        this.forecastTemperatures.add(forecast.get("forecastday").get(1).get("day").get("avgtemp_c").floatValue());
        this.forecastTemperatures.add(forecast.get("forecastday").get(2).get("day").get("avgtemp_c").floatValue());
        this.forecastImages.add(forecast.get("forecastday").get(0).get("day").get("condition").get("icon").asText().replace("64x64", "128x128"));
        this.forecastImages.add(forecast.get("forecastday").get(1).get("day").get("condition").get("icon").asText().replace("64x64", "128x128"));
        this.forecastImages.add(forecast.get("forecastday").get(2).get("day").get("condition").get("icon").asText().replace("64x64", "128x128"));
        this.forecastDescriptions.add(forecast.get("forecastday").get(0).get("day").get("condition").get("text").asText());
        this.forecastDescriptions.add(forecast.get("forecastday").get(1).get("day").get("condition").get("text").asText());
        this.forecastDescriptions.add(forecast.get("forecastday").get(2).get("day").get("condition").get("text").asText());
        this.forecastHumidities.add(forecast.get("forecastday").get(0).get("day").get("avghumidity").floatValue());
        this.forecastHumidities.add(forecast.get("forecastday").get(1).get("day").get("avghumidity").floatValue());
        this.forecastHumidities.add(forecast.get("forecastday").get(2).get("day").get("avghumidity").floatValue());


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

    public Float getHumidity() {
        return currentHumidity;
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

    public List<Float> getForecastHumidities() {
        return forecastHumidities;
    }
}
