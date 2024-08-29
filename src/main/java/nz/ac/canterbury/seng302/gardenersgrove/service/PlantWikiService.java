package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlantResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Service clas for interacting with the Perenual API
 */
@Service
public class PlantWikiService {

    private String api_key;

    private String PERENUAL_API_URL = "https://perenual.com/api/species-list";
    private final ObjectMapper objectMapper;

    @Autowired
    public PlantWikiService(@Value("${plantWiki.key}") String api_key, ObjectMapper objectMapper) {
        this.api_key = api_key;
        this.objectMapper = objectMapper;

    }

    /**
     * Queries the Perenual API for plants matching the given query string.
     * The method sends a GET request to the API, parses the response, and converts it into a list of WikiPlant objects.
     *
     * @param query The search query for the plant.
     * @return A list of WikiPlant objects that match the query.
     * @throws IOException        If there is an error reading the response from the API.
     * @throws URISyntaxException If the constructed URI is invalid.
     */
    public List<WikiPlant> getPlants(String query) throws IOException, URISyntaxException {
        List<WikiPlant> plantResults = new ArrayList<>();
        String uri = PERENUAL_API_URL +"?key="+ this.api_key + "&q=" + query;
        URL url = new URI(uri).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            WikiPlantResponse wikiPlantResponse = objectMapper.readValue(url, WikiPlantResponse.class);
            for ( JsonNode plant : wikiPlantResponse.getData()) {
                 long id = plant.get("id").asLong();
                 if (id <=3000) {
                     String name = plant.get("common_name").asText();
//                     Referenced ChatGPT to convert the JsonNode to a list
                     List<String> scientificName = objectMapper.convertValue(plant.get("scientific_name"), new TypeReference<List<String>>() {});
                     List<String> otherNames = objectMapper.convertValue(plant.get("other_name"), new TypeReference<List<String>>() {});
                     String cycle = plant.get("cycle").asText();
                     String watering = plant.get("watering").asText();
                     List<String> sunlight = objectMapper.convertValue(plant.get("sunlight"), new TypeReference<List<String>>() {});
                     String imagePath ="";
                     if (plant.get("default_image").has("original_url")) {
                         imagePath = plant.get("default_image").get("original_url").asText();}

                     WikiPlant wikiPlant = new WikiPlant(id, name, scientificName, otherNames, cycle, watering, sunlight, imagePath);
                     plantResults.add(wikiPlant);
                 }

            }
            return plantResults;
        } catch (IOException ex) {
            // this occurs when no plant matches the search
            return null;
        }

    }

}
