package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents the response from the plant identification API.
 * This class maps the JSON response to Java fields using Jackson annotations.
 */
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