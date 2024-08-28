package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Intermediary object to store the response from the plant wiki api
 */
public class WikiPlantResponse {
    @JsonProperty("data")
    private JsonNode data;

    public JsonNode getData() {return data;}
    public WikiPlantResponse() {}

    public void setData(JsonNode data) {
        this.data = data;
    }
}
