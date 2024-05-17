package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Weather {
    @JsonProperty("location")
    private JsonNode location;

    private String currentLocation;

    public Weather() {

    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(JsonNode location) {
        this.location = location;
        this.currentLocation = location.get("name").asText();
    }

    public String getCurrentLocation() {
        return currentLocation;
    }
}
