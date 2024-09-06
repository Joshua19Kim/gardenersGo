package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlantResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service clas for interacting with the Perenual API
 */
@Service
public class PlantWikiService {

    Logger logger = LoggerFactory.getLogger(PlantWikiService.class);
    private String api_key;

    private String PERENUAL_API_URL = "https://perenual.com/api/species-list";
    private final ObjectMapper objectMapper;

    @Autowired
    public PlantWikiService(@Value("${plantWiki.key}") String api_key, ObjectMapper objectMapper) {
        this.api_key = api_key;
        this.objectMapper = objectMapper;

    }

  /**
   * Queries the Perenual API for plants matching the given query string. The method sends a GET
   * request to the API, parses the response, and converts it into a list of WikiPlant objects.
   *
   * @param query The search query for the plant.
   * @return A list of WikiPlant objects that match the query.
   * @throws IOException If there is an error reading the response from the API.
   * @throws URISyntaxException If the constructed URI is invalid.
   */
  @Cacheable(value = "plantInformation", key = "#query")
  public Object getPlants(String query) throws IOException, URISyntaxException {
        List<WikiPlant> plantResults = new ArrayList<>();
        query = query.replace(" ", "%20");
        String uri = PERENUAL_API_URL +"?key="+ this.api_key + "&q=" + query;
    try {
      URL url = new URI(uri).toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      int responseCode = connection.getResponseCode();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      if (responseCode == HttpStatus.OK.value()) {
        String remainingRequests = connection.getHeaderField("X-RateLimit-Remaining");

        if (remainingRequests != null && Integer.parseInt(remainingRequests) <= 0) {
          return "The plant wiki API is down for the day :( \n Try again tomorrow";
        }

        WikiPlantResponse wikiPlantResponse = objectMapper.readValue(url, WikiPlantResponse.class);
        for (JsonNode plant : wikiPlantResponse.getData()) {
          long id = plant.get("id").asLong();
          if (id <= 3000) {
            String name = plant.get("common_name").asText();
            //                     Referenced ChatGPT to convert the JsonNode to a list
            List<String> scientificName =
                objectMapper.convertValue(
                    plant.get("scientific_name"), new TypeReference<List<String>>() {});
            List<String> otherNames =
                objectMapper.convertValue(
                    plant.get("other_name"), new TypeReference<List<String>>() {});
            String cycle = plant.get("cycle").asText();
            String watering = plant.get("watering").asText();
            List<String> sunlight =
                objectMapper.convertValue(
                    plant.get("sunlight"), new TypeReference<List<String>>() {});
            String imagePath = "";
            if (plant.get("default_image").has("small_url")) {
              imagePath = plant.get("default_image").get("small_url").asText();
            }

            WikiPlant wikiPlant =
                new WikiPlant(
                    id, name, scientificName, otherNames, cycle, watering, sunlight, imagePath);
            plantResults.add(wikiPlant);
          }
        }
        return plantResults;
      } else {
        throw new IOException("Unexpected response code: " + responseCode);
      }
    } catch (IOException ex) {
      // this occurs when no plant matches the search
      return null;
    }
  }

    /** Used to clear the cache every hour to ensure that the plant information data is not stale */
    @CacheEvict(value = {"plantInformation"}, allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.currentWeatherTTL}")
    public void emptyPlantWikiCache() {
        logger.info("Emptying plant wiki information cache");
    }
}
