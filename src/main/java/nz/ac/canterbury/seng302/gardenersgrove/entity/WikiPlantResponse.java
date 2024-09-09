package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents the response from the plant wiki API.
 * This class maps the JSON response to Java fields using Jackson annotations.
 */
public class WikiPlantResponse {
    @JsonProperty("data")
    private JsonNode data;

    public WikiPlantResponse() {}

    public JsonNode getData() {return data;}

    public void setData(JsonNode data) {
        this.data = data;
    }
}
