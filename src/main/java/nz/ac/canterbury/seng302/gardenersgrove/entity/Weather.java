package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Weather {
    @JsonProperty("location")
    private JsonNode location;

    @JsonProperty("current")
    private JsonNode current;

    private String currentLocation;
    private String weatherImage;
    private String weatherDescription;
    private Float temperature;
    private Float humidity;

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
        this.weatherImage = current.get("condition").get("icon").asText();
        this.weatherDescription = current.get("condition").get("text").asText();
        this.temperature = current.get("temp_c").floatValue();
        this.humidity = current.get("humidity").floatValue();
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getWeatherImage() {
        return weatherImage;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public Float getTemperature() {
        return temperature;
    }

    public Float getHumidity() {
        return humidity;
    }
}
