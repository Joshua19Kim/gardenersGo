package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the Perenual API
 * This service handles fetching plant data based on user queries
 * Uses caching to reduce API calls, but does not cache responses when the
 * rate limit is exceeded or the API is temporarily unavailable
 */
@Service
public class PlantWikiService {

    Logger logger = LoggerFactory.getLogger(PlantWikiService.class);
    private String apiKey;

    private String perenualApiUrl = "https://perenual.com/api/species-list";
    private String apiDownMessage = "The plant wiki is down for the day :( Try again tomorrow";
    private final ObjectMapper objectMapper;

  /**
   * Constructor for the PlantWikiService. Initializes the API key and object mapper
   * @param apiKey      The API key to access the Perenual API, injected from the application properties
   * @param objectMapper The object mapper to parse the API response, injected by Spring's dependency injection
   */
    @Autowired
    public PlantWikiService(@Value("${plantWiki.key}") String apiKey, ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;

    }

  /**
   * Queries the Perenual API for plants matching the given query string. The method sends a GET
   * request to the API, parses the response, and converts it into a list of WikiPlant objects The
   * response is cached unless the API rate limit is exceeded
   *
   * @param query The search query for the plant.
   * @return A list of WikiPlant objects that match the query or an error message if the query fails
   * @throws IOException If there is an error reading the response from the API
   * @throws URISyntaxException If the constructed URI is invalid
   */
  @Cacheable(
      value = "plantInformation",
      key = "#query",
      unless = "#result == 'The plant wiki is down for the day :( Try again tomorrow'")
  public Object getPlants(String query) throws URISyntaxException, MalformedURLException {

    List<WikiPlant> plantResults = new ArrayList<>();
    query = URLEncoder.encode(query, StandardCharsets.UTF_8);
    String uriString = perenualApiUrl + "?key=" + this.apiKey + "&q=" + query;
    URI uri = new URI(uriString);
    URL url = uri.toURL();
    String canonicalUrl = url.toURI().normalize().toString();

    // ensure the URL starts with the expected Perenual base URL
    if (!canonicalUrl.startsWith(perenualApiUrl)) {
        throw new URISyntaxException(canonicalUrl, "Invalid URL - outside of allowed domain.");
    }
    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      String rateLimitRemaining = connection.getHeaderField("X-RateLimit-Remaining");
      if (rateLimitRemaining != null && Integer.parseInt(rateLimitRemaining) <= 0) {
        return apiDownMessage;
      } else {
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
      }
    } catch (IOException ex) {
      // this occurs when no plant matches the search or when there's a connection issue
      return "An error occurred while fetching plant data. Please try again later.";
    }
  }

    /** Used to clear the cache every hour to ensure that the plant information data is not stale */
    @CacheEvict(value = {"plantInformation"}, allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.currentWeatherTTL}")
    public void emptyPlantWikiCache() {
        logger.info("Emptying plant wiki information cache");
    }


}
