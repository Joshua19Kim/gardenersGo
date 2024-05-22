package nz.ac.canterbury.seng302.gardenersgrove.service;

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

/**
 * LocationService sends an autocomplete request to server.
 */
@Service
public class LocationService {
    Logger logger = LoggerFactory.getLogger(LocationService.class);
    private String api_key;

    /**
     * Constructor of LocationService.
     * @param api_key The credential key for the Location API. It is saved as an environment variable.
     */
    @Autowired
    public LocationService(@Value("${locationIq.password}") String api_key) {this.api_key = api_key;
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://us1.locationiq.com/v1/autocomplete?q=" + urlQuery + "&key=" + this.api_key))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        // Needs to be HttpClient type as it goes back to HTML.
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    }
}
