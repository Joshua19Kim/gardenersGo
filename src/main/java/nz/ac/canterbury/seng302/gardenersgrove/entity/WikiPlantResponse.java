package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class WikiPlantResponse {
    @JsonProperty("data")
    private JsonNode data;

    public JsonNode getData() {return data;}
    public WikiPlantResponse() {}

    public void setData(JsonNode data) {
        this.data = data;
    }
}
