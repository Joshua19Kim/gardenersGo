package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentifiedPlantResponse {

    @JsonProperty("bestMatch")
    private String bestMatch;

    @JsonProperty("results")
    private JsonNode results;

    public String getBestMatch() {
        return bestMatch;
    }

    public void setBestMatch(String bestMatch) {
        this.bestMatch = bestMatch;
    }

    public JsonNode getResults() {
        return results;
    }

    public void setResults(JsonNode results) {
        this.results = results;
    }
}