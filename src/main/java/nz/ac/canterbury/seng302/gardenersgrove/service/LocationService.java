package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * LocationService sends an autocomplete request to server.
 */
@Service
public class LocationService {
    Logger logger = LoggerFactory.getLogger(LocationService.class);
    private final String api_key;
    private final HttpClient client;

    /**
     * Constructor of LocationService.
     * @param api_key The credential key for the Location API. It is saved as an environment variable.
     */
    @Autowired
    public LocationService(@Value("${locationIq.password}") String api_key, HttpClient client) {
        this.api_key = api_key;
        this.client = client;
    }

    /**
     *  This sends the query from user to the server and retrieve the suggestions from server.
     * @param query The address query created by user.
     * @return The autocomplete suggestions from the server.
     * @throws IOException IOException If an I/O error occurs during the HTTP request.
     * @throws InterruptedException InterruptedException If the thread is interrupted while waiting for the request to complete.
     */
    public HttpResponse<String> sendRequest(String query) throws IOException, InterruptedException {
        logger.info("SEND Request");
        // According to LocationIq website, the space needs to be filled with "%20"
        String urlQuery = query.replace(" ", "%20");
        String tag = "&tag=place%3Ahouse,building%3Ahouse,building%3Ayes";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://us1.locationiq.com/v1/autocomplete?q=" + urlQuery + tag + "&key=" + this.api_key))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();


        // Needs to be HttpClient type as it goes back to HTML.
        return this.client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    public String getLocationfromLatLong(String latitude, String longitude) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://us1.locationiq.com/v1/reverse?key=" + this.api_key + "&lat=" + latitude + "&lon=" + longitude + "&format=json"))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.body());

        if (jsonResponse.has("display_name")) {
            return jsonResponse.get("display_name").asText();
        }
        return "";
    }

    /**
     * This sends the coordinates to the location API and receives an address back
     * @param plantLat The latitude of the plant location.
     * @param plantLon The longitude of the plant location.
     * @return The address from the server.
     * @throws IOException IOException If an I/O error occurs during the HTTP request.
     * @throws InterruptedException InterruptedException If the thread is interrupted while waiting for the request to complete.
     */
    public String sendReverseGeocodingRequest(String plantLat, String plantLon) throws IOException, InterruptedException {
        logger.info("SEND Reverse Geocoding Request");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://us1.locationiq.com/v1/reverse?key=" + this.api_key + "&lat=" + plantLat + "&lon=" + plantLon + "&format=json"))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());
        JsonNode regionNode = jsonNode.path("address");
        String state = regionNode.path("state").asText();
        String country = regionNode.path("country").asText();
        List<String> regions = List.of("Southland", "Otago", "Canterbury", "West Coast", "Northland",
                "Tasman", "Waikato", "Wellington", "Taranaki", "Manawatu-Wanganui", "Marlborough",
                "Hawke's Bay", "Gisborne", "Bay of Plenty", "Auckland", "Nelson", "Chatham Islands");

        for (String region : regions) {
            if ((state.equalsIgnoreCase(region) || state.equalsIgnoreCase(region + " region")) && country.equalsIgnoreCase("New Zealand")) {
                return region;
            }
        }
        return null;
    }
}
